// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * RF4 (lista por categorías), RF5 (detalle), RF6 (búsqueda).
 * La SPA usa router por hash (#/ruta). Evidencia visual: screenshots.
 */
test.describe('Catálogo (RF4-RF6)', () => {

  test('grid completo con los 19 productos del seed', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText(/Rappi\.?Clon/i).first()).toBeVisible();
    const tarjetas = page.locator('img[src*="/images/productos/"]');
    await expect(tarjetas).toHaveCount(19);
    await expect(page.getByText('Pizza pepperoni')).toBeVisible();
    await expect(page.getByText('Helado artesanal')).toBeVisible();
    await page.screenshot({ path: 'screenshots/01-grid-completo.png', fullPage: true });
  });

  test('filtro por categoría Hamburguesas muestra solo 3', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /^Hamburguesas$/i }).click();
    const tarjetas = page.locator('img[src*="/images/productos/"]');
    await expect(tarjetas).toHaveCount(3);
    await expect(page.getByText('Hamburguesa clasica')).toBeVisible();
    await expect(page.getByText('Bandeja paisa')).toBeHidden();
    await page.screenshot({ path: 'screenshots/02-filtro-hamburguesas.png' });
  });

  test('búsqueda por palabra clave encuentra productos (RF6)', async ({ page }) => {
    await page.goto('/');
    await page.getByPlaceholder(/busca hamburguesas/i).fill('pizza');
    const tarjetas = page.locator('img[src*="/images/productos/"]');
    await expect(tarjetas).toHaveCount(1);
    await expect(page.getByText('Pizza pepperoni')).toBeVisible();
    await page.screenshot({ path: 'screenshots/03-busqueda.png' });
  });

  test('detalle de producto muestra imagen, descripción y precio (RF5)', async ({ page }) => {
    await page.goto('/');
    await page.getByText('Pizza pepperoni').click();
    await expect(page).toHaveURL(/#\/producto\/\d+/);
    await expect(page.getByText(/pepperoni importado/i)).toBeVisible();
    await expect(page.getByText(/35\.000/)).toBeVisible();
    await page.screenshot({ path: 'screenshots/04-detalle.png' });
  });
});
