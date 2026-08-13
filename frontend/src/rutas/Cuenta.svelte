<script>
  import { onMount } from 'svelte';
  import { sesion, cerrarSesion } from '../lib/auth.svelte.js';
  import { t } from '../lib/i18n.svelte.js';
  import { navegar } from '../lib/router.svelte.js';

  onMount(() => {
    if (!sesion.token) {
      navegar('/login');
    }
  });

  function salir() {
    cerrarSesion();
    navegar('/');
  }
</script>

<div class="tarjeta-cuenta">
  <h1>{t('tituloCuenta')}</h1>

  {#if sesion.usuario}
    <div class="fila-dato">
      <span class="etiqueta">{t('emailLabel')}</span>
      <span class="valor">{sesion.usuario.email}</span>
    </div>
    <div class="fila-dato">
      <span class="etiqueta">{t('nombreLabel')}</span>
      <span class="valor">{sesion.usuario.nombre}</span>
    </div>
    <div class="fila-dato">
      <span class="etiqueta">{t('rolLabel')}</span>
      <span class="valor">
        {sesion.usuario.rol === 'ADMIN' ? t('rolADMIN') : t('rolUSER')}
      </span>
    </div>

    <button class="btn-secundario" onclick={salir}>{t('cerrarSesion')}</button>
  {:else}
    <div class="estado-carga">{t('cargando')}</div>
  {/if}
</div>
