// Wrapper de fetch: añade Authorization Bearer cuando hay token y convierte
// respuestas HTTP/red en errores con código de estado para mensajes amigables.
import { obtenerToken } from './auth.svelte.js';

/**
 * @param {string} ruta  ruta de la API (ej: /api/productos)
 * @param {{metodo?: string, cuerpo?: object, cabeceras?: Record<string,string>}} opciones
 */
export async function apiFetch(ruta, opciones = {}) {
  const cabeceras = { ...(opciones.cabeceras ?? {}) };

  const token = obtenerToken();
  if (token) cabeceras['Authorization'] = `Bearer ${token}`;

  if (opciones.cuerpo !== undefined && !cabeceras['Content-Type']) {
    cabeceras['Content-Type'] = 'application/json';
  }

  let res;
  try {
    res = await fetch(ruta, {
      method: opciones.metodo ?? (opciones.cuerpo !== undefined ? 'POST' : 'GET'),
      headers: cabeceras,
      body: opciones.cuerpo !== undefined ? JSON.stringify(opciones.cuerpo) : undefined
    });
  } catch (e) {
    // Error de red o redirect cross-origin bloqueado por CORS.
    const error = new Error('red');
    error.status = 0;
    error.esRed = true;
    throw error;
  }

  // El backend redirige (302 a /login) en algunos errores; el navegador sigue
  // la redirección y res.ok termina true con HTML. Se trata como error amigable.
  if (res.redirected) {
    const error = new Error('redireccion');
    error.status = 0;
    error.esRedireccion = true;
    throw error;
  }

  let datos = null;
  const texto = await res.text();
  if (texto) {
    try {
      datos = JSON.parse(texto);
    } catch {
      datos = texto;
    }
  }

  if (!res.ok) {
    const error = new Error(`HTTP ${res.status}`);
    error.status = res.status;
    error.datos = datos;
    throw error;
  }

  return datos;
}
