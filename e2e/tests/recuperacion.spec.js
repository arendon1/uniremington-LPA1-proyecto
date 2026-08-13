// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * RF3: recuperación de contraseña por correo, E2E completo.
 * El correo cae en Mailpit (localhost:8025, SMTP local); el test lee el
 * mensaje por su API REST, extrae el link y completa el flujo.
 */
const MAILPIT = 'http://localhost:8025';

async function crearUsuario(page, email) {
  await page.goto('/#/registro');
  await page.getByLabel(/nombre/i).fill('Recup E2E');
  await page.getByLabel(/correo electrónico/i).fill(email);
  await page.getByLabel(/contraseña/i).fill('clave123');
  await page.getByRole('button', { name: /registrarse/i }).click();
  await expect(page).toHaveURL(/#\/login/);
}

async function ultimoEnlaceRecuperacion(request) {
  const respuesta = await request.get(`${MAILPIT}/api/v1/messages`);
  expect(respuesta.ok()).toBeTruthy();
  const mensajes = (await respuesta.json()).messages ?? [];
  expect(mensajes.length).toBeGreaterThan(0);
  const texto = mensajes[0].Snippet ?? '';
  const m = texto.match(/recuperar\?token=([A-Za-z0-9_-]+)/);
  expect(m, `link de recuperación no encontrado en: ${texto.slice(0, 300)}`).toBeTruthy();
  return `http://localhost:5173/#/recuperar?token=${m[1]}`;
}

test('forgot → correo en Mailpit → reset → login con la nueva contraseña', async ({ page, request }) => {
  const email = `recup${Date.now()}@test.com`;
  await crearUsuario(page, email);

  // 1. Solicitar recuperación
  await page.goto('/#/recuperar');
  await page.getByLabel(/correo electrónico/i).fill(email);
  await page.getByRole('button', { name: /enviar enlace/i }).click();
  await expect(page.getByText(/revisa tu correo/i)).toBeVisible();
  await page.screenshot({ path: 'screenshots/08-forgot-solicitado.png' });

  // 2. Leer el correo desde Mailpit y navegar al link (como el usuario real:
  //    pagina fresca desde el correo → reload replica la carga inicial)
  const enlace = await ultimoEnlaceRecuperacion(request);
  await page.goto(enlace);
  await page.reload();
  await expect(page).toHaveURL(/#\/recuperar\?token=/);
  await expect(page.getByLabel(/contraseña/i)).toBeVisible();
  await page.screenshot({ path: 'screenshots/09-link-desde-mailpit.png' });

  // 3. Poner la nueva contraseña
  await page.getByLabel(/contraseña/i).fill('nuevaClave99');
  await page.getByRole('button', { name: /restablecer contraseña/i }).click();
  await expect(page.getByText(/contraseña actualizada/i)).toBeVisible();
  await page.getByRole('link', { name: /volver al inicio de sesión/i }).click();
  await expect(page).toHaveURL(/#\/login/);

  // 4. Login con la nueva contraseña
  await page.getByLabel(/correo electrónico/i).fill(email);
  await page.getByLabel(/contraseña/i).fill('nuevaClave99');
  await page.getByRole('button', { name: /^ingresar$/i }).click();
  await expect(page).toHaveURL(/#\/$/);
  const token = await page.evaluate(() => localStorage.getItem('rappi_token'));
  expect(token).toBeTruthy();
  await page.screenshot({ path: 'screenshots/10-login-con-nueva-clave.png' });
});

test('forgot con email no registrado responde igual (anti-enumeración)', async ({ page }) => {
  await page.goto('/#/recuperar');
  await page.getByLabel(/correo electrónico/i).fill('nadie@test.com');
  await page.getByRole('button', { name: /enviar enlace/i }).click();
  await expect(page.getByText(/revisa tu correo/i)).toBeVisible();
});
