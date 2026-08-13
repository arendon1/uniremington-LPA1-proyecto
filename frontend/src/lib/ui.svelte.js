// Mensajes flash entre rutas (ej: registro exitoso → /login con aviso).

export const flash = $state({ tipo: '', texto: '' });

export function ponerFlash(tipo, texto) {
  flash.tipo = tipo;
  flash.texto = texto;
}

export function limpiarFlash() {
  flash.tipo = '';
  flash.texto = '';
}
