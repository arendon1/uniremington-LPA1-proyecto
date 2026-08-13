// i18n de la SPA (ES/EN/PT) con runes de Svelte 5.
// Los nombres/descripciones de productos vienen del backend en español y no
// se traducen (los datos viajan así por la API).

export const idiomasDisponibles = ['es', 'en', 'pt'];

const diccionarios = {
  es: {
    // Navegación / header
    navCuenta: 'Mi cuenta',
    navIngresar: 'Ingresar',
    navSalir: 'Salir',

    // Catálogo
    buscadorPlaceholder: 'Busca hamburguesas, pizzas, postres…',
    categoriaTodas: 'Todas',
    cargando: 'Cargando…',
    sinResultados: 'No hay productos que coincidan con tu búsqueda.',
    errorCatalogo: 'No se pudo cargar el catálogo. Intenta de nuevo.',

    // Detalle de producto
    volver: 'Volver',
    disponible: 'Disponible',
    agotado: 'Agotado',
    categoriaLabel: 'Categoría',
    unidadLabel: 'Unidad',
    stockLabel: 'Stock',
    productoNoEncontrado: 'No encontramos ese producto.',

    // Login
    tituloLogin: 'Iniciar sesión',
    emailLabel: 'Correo electrónico',
    passwordLabel: 'Contraseña',
    botonLogin: 'Ingresar',
    sinCuenta: '¿No tienes cuenta?',
    crearCuenta: 'Regístrate',
    olvidastePassword: '¿Olvidaste tu contraseña?',
    errorLogin: 'Credenciales incorrectas. Verifica tu correo y contraseña.',
    bienvenido: '¡Bienvenido de nuevo!',

    // Registro
    tituloRegistro: 'Crear cuenta',
    nombreLabel: 'Nombre',
    botonRegistrar: 'Registrarse',
    yaTienesCuenta: '¿Ya tienes cuenta?',
    iniciarSesion: 'Inicia sesión',
    passwordMin: 'Mínimo 6 caracteres',
    error409: 'Ya existe un usuario con ese email.',
    errorRegistro: 'No se pudo crear la cuenta. Revisa los datos e inténtalo de nuevo.',
    registroExito: 'Cuenta creada correctamente. Ahora inicia sesión.',

    // Recuperación
    tituloRecuperar: 'Recuperar contraseña',
    subtituloRecuperar: 'Te enviaremos por correo las instrucciones para restablecer tu contraseña.',
    enviarEnlace: 'Enviar enlace',
    exitoEnviado: 'Revisa tu correo: te enviamos las instrucciones para restablecer tu contraseña.',
    tituloNuevaPassword: 'Nueva contraseña',
    botonRestablecer: 'Restablecer contraseña',
    exitoRestablecido: 'Contraseña actualizada. Ya puedes iniciar sesión.',
    errorToken: 'El enlace es inválido, expiró o ya fue usado.',
    volverAlLogin: 'Volver al inicio de sesión',

    // Cuenta
    tituloCuenta: 'Mi cuenta',
    rolLabel: 'Rol',
    rolADMIN: 'Administrador',
    rolUSER: 'Usuario',
    cerrarSesion: 'Cerrar sesión',

    // Errores comunes (por código de estado)
    errorDatos: 'Hay datos inválidos. Revisa el formulario.',
    errorNoAutorizado: 'No autorizado. Inicia sesión para continuar.',
    errorProhibido: 'No tienes permisos para realizar esta acción.',
    errorNoEncontrado: 'No se encontró lo que buscas.',
    errorServidor: 'Ocurrió un error en el servidor. Intenta de nuevo más tarde.',
    errorRed: 'No se pudo conectar con el servidor. Verifica tu conexión.',
    errorGenerico: 'Algo salió mal. Intenta de nuevo.'
  },

  en: {
    navCuenta: 'My account',
    navIngresar: 'Sign in',
    navSalir: 'Log out',

    buscadorPlaceholder: 'Search burgers, pizzas, desserts…',
    categoriaTodas: 'All',
    cargando: 'Loading…',
    sinResultados: 'No products match your search.',
    errorCatalogo: 'Could not load the catalog. Try again.',

    volver: 'Back',
    disponible: 'Available',
    agotado: 'Out of stock',
    categoriaLabel: 'Category',
    unidadLabel: 'Unit',
    stockLabel: 'Stock',
    productoNoEncontrado: 'We could not find that product.',

    tituloLogin: 'Sign in',
    emailLabel: 'Email',
    passwordLabel: 'Password',
    botonLogin: 'Sign in',
    sinCuenta: "Don't have an account?",
    crearCuenta: 'Create account',
    olvidastePassword: 'Forgot your password?',
    errorLogin: 'Incorrect credentials. Check your email and password.',
    bienvenido: 'Welcome back!',

    tituloRegistro: 'Create account',
    nombreLabel: 'Name',
    botonRegistrar: 'Register',
    yaTienesCuenta: 'Already have an account?',
    iniciarSesion: 'Sign in',
    passwordMin: 'Minimum 6 characters',
    error409: 'A user with that email already exists.',
    errorRegistro: 'Could not create the account. Check your details and try again.',
    registroExito: 'Account created. Now sign in.',

    tituloRecuperar: 'Reset password',
    subtituloRecuperar: "We'll email you instructions to reset your password.",
    enviarEnlace: 'Send link',
    exitoEnviado: 'Check your email: we sent you instructions to reset your password.',
    tituloNuevaPassword: 'New password',
    botonRestablecer: 'Reset password',
    exitoRestablecido: 'Password updated. You can now sign in.',
    errorToken: 'The link is invalid, expired, or already used.',
    volverAlLogin: 'Back to sign in',

    tituloCuenta: 'My account',
    rolLabel: 'Role',
    rolADMIN: 'Administrator',
    rolUSER: 'User',
    cerrarSesion: 'Log out',

    errorDatos: 'Invalid data. Check the form.',
    errorNoAutorizado: 'Not authorized. Sign in to continue.',
    errorProhibido: 'You do not have permission to do this.',
    errorNoEncontrado: 'We could not find what you are looking for.',
    errorServidor: 'A server error occurred. Try again later.',
    errorRed: 'Could not reach the server. Check your connection.',
    errorGenerico: 'Something went wrong. Try again.'
  },

  pt: {
    navCuenta: 'Minha conta',
    navIngresar: 'Entrar',
    navSalir: 'Sair',

    buscadorPlaceholder: 'Busque hambúrgueres, pizzas, sobremesas…',
    categoriaTodas: 'Todas',
    cargando: 'Carregando…',
    sinResultados: 'Nenhum produto corresponde à sua busca.',
    errorCatalogo: 'Não foi possível carregar o catálogo. Tente novamente.',

    volver: 'Voltar',
    disponible: 'Disponível',
    agotado: 'Esgotado',
    categoriaLabel: 'Categoria',
    unidadLabel: 'Unidade',
    stockLabel: 'Estoque',
    productoNoEncontrado: 'Não encontramos esse produto.',

    tituloLogin: 'Entrar',
    emailLabel: 'E-mail',
    passwordLabel: 'Senha',
    botonLogin: 'Entrar',
    sinCuenta: 'Não tem conta?',
    crearCuenta: 'Cadastre-se',
    olvidastePassword: 'Esqueceu sua senha?',
    errorLogin: 'Credenciais incorretas. Verifique seu e-mail e senha.',
    bienvenido: 'Bem-vindo de volta!',

    tituloRegistro: 'Criar conta',
    nombreLabel: 'Nome',
    botonRegistrar: 'Cadastrar',
    yaTienesCuenta: 'Já tem conta?',
    iniciarSesion: 'Entre',
    passwordMin: 'Mínimo 6 caracteres',
    error409: 'Já existe um usuário com esse e-mail.',
    errorRegistro: 'Não foi possível criar a conta. Verifique os dados e tente novamente.',
    registroExito: 'Conta criada com sucesso. Agora entre.',

    tituloRecuperar: 'Recuperar senha',
    subtituloRecuperar: 'Enviaremos por e-mail as instruções para redefinir sua senha.',
    enviarEnlace: 'Enviar link',
    exitoEnviado: 'Verifique seu e-mail: enviamos as instruções para redefinir sua senha.',
    tituloNuevaPassword: 'Nova senha',
    botonRestablecer: 'Redefinir senha',
    exitoRestablecido: 'Senha atualizada. Agora você pode entrar.',
    errorToken: 'O link é inválido, expirou ou já foi usado.',
    volverAlLogin: 'Voltar ao login',

    tituloCuenta: 'Minha conta',
    rolLabel: 'Função',
    rolADMIN: 'Administrador',
    rolUSER: 'Usuário',
    cerrarSesion: 'Sair',

    errorDatos: 'Dados inválidos. Verifique o formulário.',
    errorNoAutorizado: 'Não autorizado. Entre para continuar.',
    errorProhibido: 'Você não tem permissão para realizar esta ação.',
    errorNoEncontrado: 'Não encontramos o que você procura.',
    errorServidor: 'Ocorreu um erro no servidor. Tente novamente mais tarde.',
    errorRed: 'Não foi possível conectar ao servidor. Verifique sua conexão.',
    errorGenerico: 'Algo deu errado. Tente novamente.'
  }
};

// Nombres visibles de categorías (el backend devuelve códigos en mayúsculas).
const categoriasTraducidas = {
  es: {
    HAMBURGUESAS: 'Hamburguesas',
    COMIDAS: 'Comidas',
    BEBIDAS: 'Bebidas',
    POSTRES: 'Postres'
  },
  en: {
    HAMBURGUESAS: 'Burgers',
    COMIDAS: 'Meals',
    BEBIDAS: 'Drinks',
    POSTRES: 'Desserts'
  },
  pt: {
    HAMBURGUESAS: 'Hambúrgueres',
    COMIDAS: 'Comidas',
    BEBIDAS: 'Bebidas',
    POSTRES: 'Sobremesas'
  }
};

const localesNumero = { es: 'es-CO', en: 'en-US', pt: 'pt-BR' };

// Idioma activo, persistido en localStorage. Runes de módulo (.svelte.js).
let idiomaInicial = 'es';
if (typeof localStorage !== 'undefined') {
  const guardado = localStorage.getItem('rappi_idioma');
  if (guardado && idiomasDisponibles.includes(guardado)) idiomaInicial = guardado;
}

// $state interno (no se exporta: Svelte 5 prohíbe exportar estado reasignado).
let idioma = $state(idiomaInicial);

/** Getter reactivo: leerlo en un template re-renderiza al cambiar el idioma. */
export function obtenerIdioma() {
  return idioma;
}

export function establecerIdioma(codigo) {
  if (!idiomasDisponibles.includes(codigo)) return;
  idioma = codigo;
  if (typeof localStorage !== 'undefined') {
    localStorage.setItem('rappi_idioma', codigo);
  }
  if (typeof document !== 'undefined') {
    document.documentElement.lang = codigo;
  }
}

/** Traduce una clave al idioma activo (con fallback a español). */
export function t(clave) {
  const tabla = diccionarios[idioma] ?? diccionarios.es;
  return tabla[clave] ?? diccionarios.es[clave] ?? clave;
}

/** Nombre visible de una categoría (código backend → texto traducido). */
export function nombreCategoria(codigo) {
  const tabla = categoriasTraducidas[idioma] ?? categoriasTraducidas.es;
  return tabla[codigo] ?? codigo;
}

/** Formato COP según idioma: es/pt punto de miles, en coma. */
export function formatearPrecio(precioCop) {
  const locale = localesNumero[idioma] ?? 'es-CO';
  return `COP ${new Intl.NumberFormat(locale).format(precioCop)}`;
}

/**
 * Mapea un error de red/HTTP a un mensaje amigable.
 * @param {object} e error lanzado por apiFetch: { status, esRedireccion }
 */
export function mensajeError(e) {
  if (e?.esRedireccion) return t('errorGenerico');
  const est = e?.status;
  if (!est || est === 0) return t('errorRed');
  switch (est) {
    case 400:
      return t('errorDatos');
    case 401:
      return t('errorNoAutorizado');
    case 403:
      return t('errorProhibido');
    case 404:
      return t('errorNoEncontrado');
    case 409:
      return t('error409');
    case 500:
    case 502:
    case 503:
      return t('errorServidor');
    default:
      return t('errorGenerico');
  }
}
