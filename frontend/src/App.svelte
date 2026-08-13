<script>
  import { onMount } from 'svelte';
  import { parsearRuta, navegar } from './lib/router.svelte.js';
  import { t, obtenerIdioma, establecerIdioma, idiomasDisponibles } from './lib/i18n.svelte.js';
  import { sesion, cerrarSesion } from './lib/auth.svelte.js';
  import Catalogo from './rutas/Catalogo.svelte';
  import ProductoDetalle from './rutas/ProductoDetalle.svelte';
  import Login from './rutas/Login.svelte';
  import Registro from './rutas/Registro.svelte';
  import Recuperar from './rutas/Recuperar.svelte';
  import Cuenta from './rutas/Cuenta.svelte';

  let ruta = $state({ camino: '/', parametros: new URLSearchParams() });

  function actualizarRuta() {
    ruta = parsearRuta();
    window.scrollTo(0, 0);
  }

  onMount(() => {
    actualizarRuta();
    window.addEventListener('hashchange', actualizarRuta);
    document.documentElement.lang = obtenerIdioma();
    return () => window.removeEventListener('hashchange', actualizarRuta);
  });

  function salir() {
    cerrarSesion();
    navegar('/');
  }

  const camino = $derived(ruta.camino);
  const idProducto = $derived.by(() => {
    const coincidencia = ruta.camino.match(/^\/producto\/(\d+)$/);
    return coincidencia ? coincidencia[1] : null;
  });
</script>

<header class="cabecera">
  <a class="logo" href="#/">Rappi<span class="punto">.</span><b>Clon</b></a>

  <nav class="nav" aria-label="Navegación">
    {#if sesion.usuario}
      <a href="#/cuenta">{t('navCuenta')}</a>
      <button class="btn-enlace" onclick={salir}>{t('navSalir')}</button>
    {:else}
      <a href="#/login">{t('navIngresar')}</a>
    {/if}
  </nav>

  <div class="idiomas" role="group" aria-label="Idioma">
    {#each idiomasDisponibles as codigo (codigo)}
      <button
        class:activo={obtenerIdioma() === codigo}
        onclick={() => establecerIdioma(codigo)}
        aria-pressed={obtenerIdioma() === codigo}
      >
        {codigo.toUpperCase()}
      </button>
    {/each}
  </div>
</header>

<main class="contenido">
  {#if idProducto}
    <ProductoDetalle id={idProducto} />
  {:else if camino === '/login'}
    <Login />
  {:else if camino === '/registro'}
    <Registro />
  {:else if camino === '/recuperar'}
    <Recuperar token={ruta.parametros.get('token')} />
  {:else if camino === '/cuenta'}
    <Cuenta />
  {:else}
    <Catalogo />
  {/if}
</main>
