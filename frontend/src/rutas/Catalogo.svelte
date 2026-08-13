<script>
  import { apiFetch } from '../lib/api.svelte.js';
  import { t, nombreCategoria, formatearPrecio, mensajeError } from '../lib/i18n.svelte.js';

  let productos = $state([]);
  let categorias = $state([]);
  let busqueda = $state('');
  let categoriaActiva = $state('');
  let cargando = $state(true);
  let error = $state('');
  let temporizador;

  $effect(() => {
    cargarCategorias();
  });

  // Debounce de 300 ms: cada cambio en búsqueda/categoría recarga el catálogo.
  $effect(() => {
    const q = busqueda;
    const cat = categoriaActiva;
    clearTimeout(temporizador);
    temporizador = setTimeout(() => cargar(q, cat), 300);
    return () => clearTimeout(temporizador);
  });

  async function cargarCategorias() {
    try {
      categorias = await apiFetch('/api/categorias');
    } catch {
      categorias = [];
    }
  }

  async function cargar(q, cat) {
    cargando = true;
    error = '';
    try {
      const texto = (q ?? '').trim();
      let url = '/api/productos';
      if (texto) {
        url = `/api/productos/buscar?q=${encodeURIComponent(texto)}`;
      } else if (cat) {
        url = `/api/productos?categoria=${encodeURIComponent(cat)}`;
      }
      productos = await apiFetch(url);
    } catch (e) {
      productos = [];
      error = mensajeError(e);
    } finally {
      cargando = false;
    }
  }
</script>

<div class="caja-buscador">
  <span class="icono-lupa" aria-hidden="true">
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
      <circle cx="11" cy="11" r="7" />
      <line x1="21" y1="21" x2="16.5" y2="16.5" />
    </svg>
  </span>
  <input
    class="buscador"
    type="search"
    placeholder={t('buscadorPlaceholder')}
    bind:value={busqueda}
    aria-label={t('buscadorPlaceholder')}
  />
</div>

<div class="chips" role="tablist" aria-label={t('categoriaLabel')}>
  <button
    class="chip"
    class:activo={categoriaActiva === ''}
    onclick={() => { categoriaActiva = ''; }}
  >
    {t('categoriaTodas')}
  </button>
  {#each categorias as cat (cat)}
    <button
      class="chip"
      class:activo={categoriaActiva === cat}
      onclick={() => { categoriaActiva = cat; }}
    >
      {nombreCategoria(cat)}
    </button>
  {/each}
</div>

{#if error}
  <div class="aviso aviso-error" role="alert">{error}</div>
{/if}

{#if cargando}
  <div class="estado-carga">{t('cargando')}</div>
{:else if productos.length === 0}
  <div class="estado-vacio">{t('sinResultados')}</div>
{:else}
  <div class="grid-productos">
    {#each productos as p (p.id)}
      <a class="tarjeta" href={`#/producto/${p.id}`}>
        <img src={p.imagenUrl} alt={p.nombre} loading="lazy" />
        <div class="tarjeta-cuerpo">
          <h3>{p.nombre}</h3>
          <p class="precio">{formatearPrecio(p.precioCop)}</p>
        </div>
      </a>
    {/each}
  </div>
{/if}
