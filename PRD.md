# PRD: RappiClon

Aplicación web del curso Lenguaje de Programación Avanzado I (Uniremington, 2026-2).
Dominio: catálogo de productos y pedidos, estilo e-commerce o app de domicilios.
El proyecto crece unidad por unidad. Hoy entrega la Unidad 2, que suma
internacionalización a la seguridad de la Unidad 1.

## Qué hay construido (U1 + U2)

**Seguridad (Unidad 1).** Login por formulario con Spring Security sobre
Spring Boot. Dos usuarios de prueba cargados en memoria:

- `admin` / `admin123` con rol `ADMIN`
- `user` / `user123` con rol `USER`

Tres rutas protegidas por rol:

| Ruta | Acceso |
|------|--------|
| `/public` | cualquiera, sin login |
| `/user` | `USER` o `ADMIN` |
| `/admin` | solo `ADMIN` |

Más `/login` (formulario Thymeleaf con CSRF) y `/access-denied` (página propia
de denegación, no el 403 feo del contenedor). Cuando un usuario sin el rol
adecuado toca `/admin`, no recibe un error crudo del servlet. La app hace un
forward a `/access-denied` con HTTP 200 y un body amigable. Esa fue una
decisión consciente, no el comportamiento por defecto de Spring Security.

**Internacionalización (Unidad 2).** La interfaz se muestra en español,
inglés y portugués. Un botón por idioma en la cabecera (`ES`, `EN`, `PT`)
recarga la página con el parámetro `?lang=xx`. La configuración usa tres
piezas de Spring:

1. **MessageSource**. Boot crea un `ResourceBundleMessageSource` a partir de
   `spring.messages.basename=messages`, con `encoding=UTF-8` y
   `fallback-to-system-locale=false`. Así resuelve los cuatro bundles
   (`messages.properties` y uno por idioma) y conserva los acentos.
2. **LocaleResolver**. Un `CookieLocaleResolver` con español por defecto y una
   cookie de 30 días. El idioma elegido sobrevive al logout, cosa que no
   pasaría con un `SessionLocaleResolver`.
3. **LocaleChangeInterceptor**. Registrado con `WebMvcConfigurer`, lee
   `?lang=` y actualiza el locale de la petición.

El catálogo tiene 10 platos con nombre, descripción y precio traducidos. Los
precios se formatean con `NumberFormat` según el idioma, y se muestran siempre
en pesos colombianos. Están localizadas cinco vistas: `/` (tienda), `/user`
(mi cuenta), `/admin` (panel), `/login` y `/access-denied`.

## Stack

Backend en Java 21, Spring Boot 4.1.0, Spring Security 7.1.0 (vía Boot),
Thymeleaf para las vistas y Maven para el build. Sin base de datos todavía:
los usuarios viven en un `InMemoryUserDetailsManager`, que es lo que pide la
Tarea 01.

Las versiones se fijaron contra los releases de GitHub y la documentación
oficial de Spring, el 8 de julio. Spring Boot 4 es nuevo y trae algunos
deltas frente a la 3 (el más visible es que `PathRequest` cambió de paquete),
pero el código es idiomático y mapea sin problema al material del curso.

## Cómo arrancar

```bash
./mvnw spring-boot:run
```

La app levanta en http://localhost:8080. En el log se ve `Tomcat started on
port 8080` y `Started RappiClonApplication`. Los dos warnings de
`withDefaultPasswordEncoder` son esperados: la Tarea 01 no exige hashear
contraseñas, y por ahora se dejó así para no complicar la demo. Cuando toque
persistencia real se cambia a BCrypt.

## Estructura del código

```
src/main/java/one/austral/lpa1/
  RappiClonApplication.java            entrada (@SpringBootApplication)
  config/ConfiguracionSeguridad.java   SecurityFilterChain, reglas, login, logout
  config/ConfiguracionMvc.java         LocaleResolver + interceptor de idioma (U2)
  config/ContextoWeb.java              ruta actual disponible en las vistas (U2)
  security/ProveedorUsuariosEnMemoria.java   admin y user en memoria
  controller/ControladorPrincipal.java endpoints de todas las rutas
  model/Producto.java                  modelo del catálogo (U2)
  service/Catalogo.java                los 10 platos (U2)
  util/FormateadorPrecios.java         precios según el idioma (U2)
src/main/resources/
  application.properties               incluye claves spring.messages.*
  messages.properties, messages_es.properties,
  messages_en.properties, messages_pt.properties   bundles de idiomas (U2)
  templates/                           index, cuenta, panel, login, access-denied
  templates/fragments/                 cabecera, pie, switcher, producto (U2)
  static/css/                          base.css, app.css
  static/images/productos/             foto de cada plato (WebP + JPG)
src/test/java/one/austral/lpa1/
  PruebasSeguridadTest.java            MockMvc, matriz 3x3 de permisos
  PruebasI18nTest.java                 MockMvc, idiomas y persistencia (U2)
```

Los paquetes van en inglés (`controller`, `config`, `security`) porque es la
convención de Spring y la asumen las herramientas. Las clases, métodos,
variables y comentarios van en español, que es el idioma del curso y del
revisor.

## Configuración de seguridad

Todo vive en un solo bean `SecurityFilterChain`. El DSL de Spring Security 7
es lambda, sin el `.and()` de los tutoriales viejos de Boot 3.

`authorizeHttpRequests` define quién pasa. El orden importa y la primera regla
que matchea gana. `/public` es `permitAll`, `/user` es `hasAnyRole(USER,
ADMIN)`, `/admin` es `hasRole(ADMIN)`, y al final `anyRequest().authenticated()`
deja deny por defecto: lo que no está explícitamente permitido está prohibido.

`formLogin` sirve el formulario Thymeleaf en `/login`, con `permitAll` para no
crear un loop. `logout` hace POST a `/logout`, invalida la sesión y redirige
a `/login?logout`. `exceptionHandling` lleva el `AccessDeniedHandler` custom
que mencioné arriba. CSRF queda activado, que es el default y lo correcto para
formularios.

## Internacionalización

El detalle de la configuración i18n está en la sección de arriba. Lo que vale
la pena explicar acá es cómo se conecta con las vistas. En cada plantilla
Thymeleaf los textos se escriben como `th:text="#{clave}"`, y Spring traduce
la clave con el locale de la petición. Los mensajes con datos, como el saludo
de Mi cuenta, usan `#{cuenta.saludo(${nombre})}`.

Las tres claves de `application.properties` bastan para que Boot cree el
`MessageSource`:

```properties
spring.messages.basename=messages
spring.messages.encoding=UTF-8
spring.messages.fallback-to-system-locale=false
```

`fallback-to-system-locale=false` evita un comportamiento raro: si la máquina
donde corre la app está en otro idioma y llega una clave sin traducir, Spring
usa el bundle por defecto en vez de mezclar idiomas.

## Tests

Corren con MockMvc y son 32 casos, cero fallos: 22 de seguridad, 9 de i18n y
1 de contexto.

```bash
./mvnw test
```

`PruebasSeguridadTest` cubre la matriz completa de permisos (3 rutas por 3
estados: anónimo, `USER`, `ADMIN`), más login, logout y denegación.
`PruebasI18nTest` recorre las rutas públicas en los tres idiomas, verifica que
no queden claves sin resolver (`??clave??`), que el switcher esté presente,
que el catálogo tenga 10 platos, que los precios usen el separador del idioma
y que el idioma persista tras el logout.

## Decisiones que valen la pena mencionar

**Contraseñas en plano.** El warning al arrancar es real y es esperado. La
Tarea 01 pide autenticación en memoria y no menciona hashing. Cuando el
proyecto incorpore base de datos, `admin/admin123` y `user/user123` se
mantienen como valores de entrada pero hasheados con BCrypt.

**`AccessDeniedHandler` custom en vez de `accessDeniedPage`.** La forma corta
setea 403 antes de hacer el forward, y algunos navegadores muestran su propia
página de error. El handler custom deja que Thymeleaf responda 200 con un
body limpio. Es un poco más de código pero la UX es mejor.

**`CookieLocaleResolver` en vez de `SessionLocaleResolver`.** La cookie
sobrevive a la invalidación de la sesión. Con la sesión, el usuario perdía el
idioma en cada logout. No se usó `AcceptHeaderLocaleResolver` porque rechaza
`setLocale()`, y los botones de idioma de la tarea dependen de ese método.

**Stack en la última versión.** Spring Boot 4.1 y Java 21. Hay deltas frente a
Boot 3 (el paquete de `PathRequest`, el DSL sin `.and()`), pero el curso
trabaja con Spring actual y el código es estándar.

## Resto del curso

El proyecto sigue creciendo. Las próximas unidades agregan una API REST con
frontend y, al final, una capa reactiva con WebFlux. Cada entrega cierra con
un video de sustentación y se entrega sobre el mismo repositorio.
