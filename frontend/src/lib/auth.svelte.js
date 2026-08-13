// Sesión de usuario: token JWT + datos, persistidos en localStorage.

const CLAVE_TOKEN = 'rappi_token';
const CLAVE_USUARIO = 'rappi_user';

function leerUsuarioGuardado() {
  try {
    const crudo = localStorage.getItem(CLAVE_USUARIO);
    return crudo ? JSON.parse(crudo) : null;
  } catch {
    return null;
  }
}

export const sesion = $state({
  token: typeof localStorage !== 'undefined' ? localStorage.getItem(CLAVE_TOKEN) : null,
  usuario: typeof localStorage !== 'undefined' ? leerUsuarioGuardado() : null
});

export function guardarSesion(token, usuario) {
  sesion.token = token;
  sesion.usuario = usuario;
  localStorage.setItem(CLAVE_TOKEN, token);
  localStorage.setItem(CLAVE_USUARIO, JSON.stringify(usuario));
}

export function cerrarSesion() {
  sesion.token = null;
  sesion.usuario = null;
  localStorage.removeItem(CLAVE_TOKEN);
  localStorage.removeItem(CLAVE_USUARIO);
}

export function estaAutenticado() {
  return Boolean(sesion.token);
}

export function obtenerToken() {
  return sesion.token;
}
