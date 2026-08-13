<script>
  import { apiFetch } from '../lib/api.svelte.js';
  import { t, nombreCategoria, formatearPrecio, mensajeError } from '../lib/i18n.svelte.js';

  let { id } = $props();

  let producto = $state(null);
  let cargando = $state(true);
  let error = $state('');

  $effect(() => {
    cargar();
  });

  async function cargar() {
    cargando = true;
    error = '';
    producto = null;
    try {
      producto = await apiFetch(`/api/productos/${id}`);
    } catch (e) {
      error = mensajeError(e);
    } finally {
      cargando = false;
    }
  }
</script>

<a class="btn-volver" href="#/">← {t('volver')}</a>

{#if cargando}
  <div class="estado-carga">{t('cargando')}</div>
{:else if error}
  <div class="aviso aviso-error" role="alert">{error}</div>
{:else if producto}
  <article class="detalle">
    <img src={producto.imagenUrl} alt={producto.nombre} />
    <div class="detalle-cuerpo">
      <h1>{producto.nombre}</h1>
      <p class="precio-grande">{formatearPrecio(producto.precioCop)}</p>
      <p class="descripcion">{producto.descripcion}</p>

      <div class="meta-producto">
        <span class="badge">{t('categoriaLabel')}: {nombreCategoria(producto.categoria)}</span>
        <span class="badge">{t('unidadLabel')}: {producto.unidad}</span>
        <span class="badge badge-stock" class:disponible={producto.stock > 0} class:agotado={producto.stock <= 0}>
          {producto.stock > 0 ? t('disponible') : t('agotado')}
        </span>
      </div>
    </div>
  </article>
{:else}
  <div class="estado-vacio">{t('productoNoEncontrado')}</div>
{/if}
