<script>
  import { apiFetch } from '../lib/api.svelte.js';
  import { t, mensajeError } from '../lib/i18n.svelte.js';
  import { navegar } from '../lib/router.svelte.js';

  // Sin ?token= → modo "solicitar enlace" (POST /forgot).
  // Con ?token=  → modo "nueva contraseña" (POST /reset).
  let { token } = $props();

  let email = $state('');
  let nuevaPassword = $state('');
  let error = $state('');
  let enviando = $state(false);
  let enviado = $state(false);
  let restablecido = $state(false);

  const modoToken = $derived(Boolean(token));

  async function solicitarEnlace(evento) {
    evento.preventDefault();
    enviando = true;
    error = '';
    try {
      await apiFetch('/api/auth/forgot', { metodo: 'POST', cuerpo: { email } });
      enviado = true;
    } catch (e) {
      error = mensajeError(e);
    } finally {
      enviando = false;
    }
  }

  async function restablecer(evento) {
    evento.preventDefault();
    enviando = true;
    error = '';
    try {
      await apiFetch('/api/auth/reset', {
        metodo: 'POST',
        cuerpo: { token, nuevaPassword }
      });
      restablecido = true;
    } catch (e) {
      // 400 (token inválido/expirado/usado) o redirección del backend.
      error = e.status === 400 || e.esRedireccion ? t('errorToken') : mensajeError(e);
    } finally {
      enviando = false;
    }
  }
</script>

<div class="formulario">
  <h1>{modoToken ? t('tituloNuevaPassword') : t('tituloRecuperar')}</h1>
  <p class="subtitulo">{modoToken ? '' : t('subtituloRecuperar')}</p>

  {#if enviado}
    <div class="aviso aviso-exito" role="status">{t('exitoEnviado')}</div>
    <p class="enlace-formulario">
      <a href="#/login">{t('volverAlLogin')}</a>
    </p>
  {:else if restablecido}
    <div class="aviso aviso-exito" role="status">{t('exitoRestablecido')}</div>
    <p class="enlace-formulario">
      <a href="#/login">{t('volverAlLogin')}</a>
    </p>
  {:else}
    {#if error}
      <div class="aviso aviso-error" role="alert">{error}</div>
    {/if}

    {#if modoToken}
      <form onsubmit={restablecer}>
        <div class="campo">
          <label for="rec-password">{t('passwordLabel')}</label>
          <input
            id="rec-password"
            type="password"
            autocomplete="new-password"
            minlength="6"
            bind:value={nuevaPassword}
            required
          />
          <p class="ayuda">{t('passwordMin')}</p>
        </div>

        <button class="btn-primario" type="submit" disabled={enviando}>
          {enviando ? t('cargando') : t('botonRestablecer')}
        </button>
      </form>
    {:else}
      <form onsubmit={solicitarEnlace}>
        <div class="campo">
          <label for="rec-email">{t('emailLabel')}</label>
          <input
            id="rec-email"
            type="email"
            autocomplete="email"
            bind:value={email}
            required
          />
        </div>

        <button class="btn-primario" type="submit" disabled={enviando}>
          {enviando ? t('cargando') : t('enviarEnlace')}
        </button>
      </form>
    {/if}

    <p class="enlace-formulario">
      <a href="#/login">{t('volverAlLogin')}</a>
    </p>
  {/if}
</div>
