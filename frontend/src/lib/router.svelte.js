// Mini enrutador por hash: #/ruta y #/ruta?param=valor.

export function parsearRuta() {
  const hash = window.location.hash.replace(/^#/, '') || '/';
  const [camino, consulta] = hash.split('?');
  return {
    camino: camino || '/',
    parametros: new URLSearchParams(consulta || '')
  };
}

export function navegar(ruta) {
  window.location.hash = ruta;
}
