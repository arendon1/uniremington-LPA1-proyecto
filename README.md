# RappiClon

App web del curso Lenguaje de Programación Avanzado I (Uniremington, 2026-2).
Dominio: catálogo de productos y pedidos, estilo e-commerce. El proyecto crece
unidad por unidad. Esta entrega corresponde a la Unidad 2: internacionalización
en español, inglés y portugués, sobre la app de la Unidad 1 (seguridad con
Spring Security).

## Qué hace

La app se muestra en tres idiomas. En la cabecera hay tres botones, `ES`, `EN`
y `PT`, y cada uno recarga la página en ese idioma. El cambio se aplica a todas
las páginas: la tienda, mi cuenta, el panel de administración, el login y la
página de acceso denegado. El idioma elegido se guarda en una cookie y se
mantiene incluso después de cerrar sesión.

El catálogo tiene 10 platos (bandeja paisa, sushi, hamburguesa, pizza, arepa,
tacos, pollo, poke, pasta y sándwich cubano). Cada plato tiene foto, nombre,
descripción y precio, todo traducido según el idioma. Los precios se muestran
en pesos colombianos con el separador de miles de cada idioma.

De la Unidad 1 se conserva todo: login por formulario, dos usuarios en memoria
(`admin` / `admin123` con rol `ADMIN`, `user` / `user123` con rol `USER`),
rutas protegidas por rol (`/public`, `/user`, `/admin`), CSRF y la página
propia de acceso denegado.

## Cambios respecto a la entrega anterior (Tarea 01)

La Tarea 01 entregó la seguridad. La Tarea 02 le suma:

- **Tres idiomas con botones.** Un botón por idioma en la cabecera, en todas
  las páginas.
- **Archivos de mensajes por idioma.** Cada texto vive en `messages_es.properties`,
  `messages_en.properties` o `messages_pt.properties`, y un archivo por defecto
  (`messages.properties`) con todas las claves en inglés.
- **El idioma se recuerda.** Se guarda en una cookie de 30 días y sobrevive al
  logout. No se pierde al cerrar sesión.
- **Catálogo de 10 platos.** La tienda ahora muestra productos con foto,
  descripción y precio, en lugar de tarjetas de navegación.
- **Precios localizados.** El separador de miles cambia según el idioma
  (1.200 en español, 1,200 en inglés).
- **Cinco vistas localizadas.** Tienda, mi cuenta, panel, login y acceso
  denegado.

Lo que no cambió: el login, los roles, el CSRF y el deny-by-default de la
Unidad 1 siguen intactos.

## Stack

- Java 21
- Spring Boot 4.1.0
- Spring Security 7.1.0
- Thymeleaf (vistas)
- Maven (build)

Sin base de datos. Los usuarios viven en un `InMemoryUserDetailsManager`, que
es lo que pide la Tarea 01.

## Arrancar

```bash
./mvnw spring-boot:run
```

La app queda en http://localhost:8080. En el log se ve `Tomcat started on
port 8080` y `Started RappiClonApplication`.

Los warnings de `withDefaultPasswordEncoder` al arrancar son esperados. La
Tarea 01 pide autenticación en memoria y no exige hashear contraseñas. Cuando
el proyecto incorpore persistencia se migrará a BCrypt.

## Tests

```bash
./mvnw test
```

Son 32 casos con MockMvc, todos en verde: 22 de seguridad, 9 de
internacionalización y 1 de contexto. Los de seguridad cubren la matriz de
permisos (3 rutas por 3 estados), login, logout y denegación. Los de i18n
recorren las rutas públicas en los tres idiomas, verifican que no quede
ninguna clave sin traducir y que el idioma persista tras el logout.

## Estructura

```
src/main/java/one/austral/lpa1/
  RappiClonApplication.java            entrada
  config/ConfiguracionSeguridad.java   SecurityFilterChain, reglas, login, logout
  config/ConfiguracionMvc.java         LocaleResolver + interceptor de idioma
  config/ContextoWeb.java              ruta actual en las vistas
  security/ProveedorUsuariosEnMemoria.java   usuarios en memoria
  controller/ControladorPrincipal.java endpoints
  model/Producto.java                  modelo del catálogo
  service/Catalogo.java                los 10 platos
  util/FormateadorPrecios.java         precios según el idioma
src/main/resources/
  application.properties               incluye claves spring.messages.*
  messages*.properties                 bundles de idiomas (es, en, pt + default)
  templates/                           index, cuenta, panel, login, access-denied
  templates/fragments/                 cabecera, pie, switcher, producto
  static/css/                          base.css, app.css
  static/images/productos/             foto de cada plato
src/test/java/one/austral/lpa1/
  PruebasSeguridadTest.java            MockMvc, matriz de permisos
  PruebasI18nTest.java                 MockMvc, idiomas y persistencia
```

Los paquetes van en inglés (`controller`, `config`, `security`) siguiendo la
convención de Spring. Las clases, métodos, variables y comentarios van en
español, que es el idioma del curso y del revisor.

## Documentación

- [PRD.md](./PRD.md) descripción del proyecto y de lo construido en U1 y U2.
