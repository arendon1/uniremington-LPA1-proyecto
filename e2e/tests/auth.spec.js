// @ts-check
const { test, expect } = require('@playwright/test');

const emailUnico = () => `e2e${Date.now()}@test.com`;

test.describe('Auth (RF1-RF2 + JWT)', () => {

  test('registro → login → catálogo, con token en localStorage', async ({ page }) => {
    const email = emailUnico();
    await page.goto('/#/registro');
    await page.getByLabel(/nombre/i).fill('E2E Test');
    await page.getByLabel(/correo electrónico/i).fill(email);
    await page.getByLabel(/contraseña/i).fill('clave123');
    await page.getByRole('button', { name: /registrarse/i }).click();
    await expect(page).toHaveURL(/#\/login/);
    await expect(page.getByText(/cuenta creada correctamente/i)).toBeVisible();

    await page.getByLabel(/correo electrónico/i).fill(email);
    await page.getByLabel(/contraseña/i).fill('clave123');
    await page.getByRole('button', { name: /^ingresar$/i }).click();
    await expect(page).toHaveURL(/#\/$/);
    const token = await page.evaluate(() => localStorage.getItem('rappi_token'));
    expect(token).toBeTruthy();
    await page.screenshot({ path: 'screenshots/05-login-ok.png' });
  });

  test('login con credenciales inválidas muestra error (401)', async ({ page }) => {
    await page.goto('/#/login');
    await page.getByLabel(/correo electrónico/i).fill('nadie@test.com');
    await page.getByLabel(/contraseña/i).fill('equivocada');
    await page.getByRole('button', { name: /^ingresar$/i }).click();
    await expect(page.getByText(/credenciales incorrectas/i)).toBeVisible();
    await page.screenshot({ path: 'screenshots/06-login-error.png' });
  });

  test('cuenta muestra el perfil y cierra sesión limpiando el token', async ({ page, request }) => {
    // el seed user/admin no pasa el input type=email de la SPA: creamos un email real
    const email = emailUnico();
    const reg = await request.post('http://localhost:8080/api/auth/register', {
      data: { email, password: 'clave123', nombre: 'Cuenta E2E' },
    });
    expect(reg.status()).toBe(201);

    await page.goto('/#/login');
    await page.getByLabel(/correo electrónico/i).fill(email);
    await page.getByLabel(/contraseña/i).fill('clave123');
    await page.getByRole('button', { name: /^ingresar$/i }).click();
    await expect(page).toHaveURL(/#\/$/);

    await page.getByRole('link', { name: /mi cuenta/i }).click();
    await expect(page.getByText(email)).toBeVisible();
    await page.getByRole('button', { name: /salir/i }).click();
    // Salir limpia el token y vuelve al catálogo con el header sin sesión
    await expect(page).toHaveURL(/#\/$/);
    await expect(page.getByRole('link', { name: /ingresar/i })).toBeVisible();
    const token = await page.evaluate(() => localStorage.getItem('rappi_token'));
    expect(token).toBeNull();
    await page.screenshot({ path: 'screenshots/07-logout.png' });
  });
});
