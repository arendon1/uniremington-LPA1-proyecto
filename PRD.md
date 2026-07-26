# PRD: RappiClon

Aplicación web del curso Lenguaje de Programación Avanzado I (Uniremington, 2026-2).
Dominio: catálogo de productos y pedidos, estilo e-commerce o app de domicilios.
El proyecto crece unidad por unidad. Hoy entrega la Unidad 1, que es seguridad.

## Qué hay construido (U1)

Login por formulario con Spring Security sobre Spring Boot. Dos usuarios de
prueba cargados en memoria:

- `admin` / `admin123` con rol `ADMIN`
- `user` / `user123` con rol `USER`

Tres rutas protegidas por rol:

| Ruta | Acceso |
|------|--------|
| `/public` | cualquiera, sin login |
| `/user` | `USER` o `ADMIN` |
| `/admin` | solo `ADMIN` |

Más `/login` (formulario Thymeleaf con CSRF) y `/access-denied` (página propia
de denegación, no el 403 feo del contenedor). La landing `/` muestra tarjetas
de navegación y el nombre del usuario activo, y un botón de logout.

Cuando un usuario sin el rol adecuado toca `/admin`, no le devuelve un error
crudo del servlet. Hace un forward a `/access-denied` con HTTP 200 y un body
amigable. Esa fue una decisión de diseño consciente, no el comportamiento por
defecto de Spring Security.

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
  security/ProveedorUsuariosEnMemoria.java   admin y user en memoria
  controller/ControladorPrincipal.java endpoints /public /user /admin /login /access-denied /
src/main/resources/
  application.properties
  templates/login.html, access-denied.html, index.html, ruta.html
  static/css/base.css, app.css
src/test/java/one/austral/lpa1/
  PruebasSeguridadTest.java            MockMvc, matriz 3x3 de permisos
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
cregar un loop. `logout` hace POST a `/logout`, invalida la sesión y redirige
a `/login?logout`. `exceptionHandling` lleva el `AccessDeniedHandler` custom
que mencioné arriba. CSRF queda activado, que es el default y lo correcto para
formularios.

## Tests

`PruebasSeguridadTest` corre con MockMvc y cubre la matriz completa: 3 rutas
por 3 estados (anónimo, `USER`, `ADMIN`), más login, logout y denegación.
Veintidós casos, cero fallos.

```bash
./mvnw test
```

## Decisiones que valen la pena mencionar

**Contraseñas en plano.** El warning al arrancar es real y es esperado. La
Tarea 01 pide autenticación en memoria y no menciona hashing. Cuando el
proyecto incorpore base de datos, `admin/admin123` y `user/user123` se
mantienen como valores de entrada pero hasheados con BCrypt.

**`AccessDeniedHandler` custom en vez de `accessDeniedPage`.** La forma corta
setea 403 antes de hacer el forward, y algunos navegadores muestran su propia
página de error. El handler custom deja que Thymeleaf responda 200 con un
body limpio. Es un poco más de código pero la UX es mejor.

**Stack en la última versión.** Spring Boot 4.1 y Java 21. Hay deltas frente a
Boot 3 (el paquete de `PathRequest`, el DSL sin `.and()`), pero el curso trabaja
con Spring actual y el código es estándar.

## Resto del curso

El proyecto sigue creciendo. Las próximas unidades agregan internacionalización
(ES/EN/PT), una API REST con frontend, y al final una capa reactiva con
WebFlux. Cada entrega cierra con un video de sustentación y se entrega sobre
el mismo repositorio.
