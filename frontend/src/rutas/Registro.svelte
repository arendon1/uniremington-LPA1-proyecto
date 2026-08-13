<script>
  import { apiFetch } from '../lib/api.svelte.js';
  import { t, mensajeError } from '../lib/i18n.svelte.js';
  import { ponerFlash } from '../lib/ui.svelte.js';
  import { navegar } from '../lib/router.svelte.js';

  let nombre = $state('');
  let email = $state('');
  let password = $state('');
  let error = $state('');
  let enviando = $state(false);

  async function enviar(evento) {
    evento.preventDefault();
    enviando = true;
    error = '';
    try {
      await apiFetch('/api/auth/register', {
        metodo: 'POST',
        cuerpo: { email, password, nombre }
      });
      ponerFlash('exito', t('registroExito'));
      navegar('/login');
    } catch (e) {
      // 409 = email duplicado; redirección del backend = no se puede saber el
      // código exacto, se muestra un aviso general amigable.
      if (e.status === 409) {
        error = t('error409');
      } else {
        error = mensajeError(e);
      }
    } finally {
      enviando = false;
    }
  }
</script>

<div class="formulario">
  <h1>{t('tituloRegistro')}</h1>
  <p class="subtitulo">{t('yaTienesCuenta')} <a href="#/login">{t('iniciarSesion')}</a></p>

  {#if error}
    <div class="aviso aviso-error" role="alert">{error}</div>
  {/if}

  <form onsubmit={enviar}>
    <div class="campo">
      <label for="reg-nombre">{t('nombreLabel')}</label>
      <input id="reg-nombre" type="text" autocomplete="name" bind:value={nombre} required />
    </div>

    <div class="campo">
      <label for="reg-email">{t('emailLabel')}</label>
      <input id="reg-email" type="email" autocomplete="email" bind:value={email} required />
    </div>

    <div class="campo">
      <label for="reg-password">{t('passwordLabel')}</label>
      <input
        id="reg-password"
        type="password"
        autocomplete="new-password"
        minlength="6"
        bind:value={password}
        required
      />
      <p class="ayuda">{t('passwordMin')}</p>
    </div>

    <button class="btn-primario" type="submit" disabled={enviando}>
      {enviando ? t('cargando') : t('botonRegistrar')}
    </button>
  </form>
</div>
