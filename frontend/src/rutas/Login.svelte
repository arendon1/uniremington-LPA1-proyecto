<script>
  import { onMount } from 'svelte';
  import { apiFetch } from '../lib/api.svelte.js';
  import { t, mensajeError } from '../lib/i18n.svelte.js';
  import { guardarSesion } from '../lib/auth.svelte.js';
  import { flash, limpiarFlash } from '../lib/ui.svelte.js';
  import { navegar } from '../lib/router.svelte.js';

  let email = $state('');
  let password = $state('');
  let error = $state('');
  let enviando = $state(false);

  let mensajeFlash = $state({ tipo: '', texto: '' });

  onMount(() => {
    if (flash.texto) {
      mensajeFlash = { tipo: flash.tipo, texto: flash.texto };
      limpiarFlash();
    }
  });

  async function enviar(evento) {
    evento.preventDefault();
    enviando = true;
    error = '';
    try {
      const datos = await apiFetch('/api/auth/login', {
        metodo: 'POST',
        cuerpo: { email, password }
      });
      guardarSesion(datos.token, {
        email: datos.email,
        nombre: datos.nombre,
        rol: datos.rol
      });
      navegar('/');
    } catch (e) {
      // Credenciales incorrectas (401) o redirección del backend: mismo aviso.
      error = e.status === 401 || e.esRedireccion ? t('errorLogin') : mensajeError(e);
    } finally {
      enviando = false;
    }
  }
</script>

<div class="formulario">
  <h1>{t('tituloLogin')}</h1>
  <p class="subtitulo">{t('bienvenido')}</p>

  {#if mensajeFlash.texto}
    <div class="aviso aviso-exito" role="status">{mensajeFlash.texto}</div>
  {/if}

  {#if error}
    <div class="aviso aviso-error" role="alert">{error}</div>
  {/if}

  <form onsubmit={enviar}>
    <div class="campo">
      <label for="login-email">{t('emailLabel')}</label>
      <input
        id="login-email"
        type="email"
        autocomplete="username"
        bind:value={email}
        required
      />
    </div>

    <div class="campo">
      <label for="login-password">{t('passwordLabel')}</label>
      <input
        id="login-password"
        type="password"
        autocomplete="current-password"
        bind:value={password}
        required
      />
    </div>

    <button class="btn-primario" type="submit" disabled={enviando}>
      {enviando ? t('cargando') : t('botonLogin')}
    </button>
  </form>

  <p class="enlace-formulario">
    {t('sinCuenta')} <a href="#/registro">{t('crearCuenta')}</a>
    <a class="bloque" href="#/recuperar">{t('olvidastePassword')}</a>
  </p>
</div>
