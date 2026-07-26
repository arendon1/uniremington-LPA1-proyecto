# RappiClon

App web del curso Lenguaje de Programación Avanzado I (Uniremington, 2026-2).
Dominio: catálogo de productos y pedidos, estilo e-commerce. El proyecto crece
unidad por unidad. Esta entrega corresponde a la Unidad 1: seguridad con
Spring Security.

## Qué hace

Login por formulario sobre Spring Boot. Dos usuarios de prueba en memoria:

- `admin` / `admin123` (rol `ADMIN`)
- `user` / `user123` (rol `USER`)

Tres rutas protegidas por rol:

- `/public` abre sin login.
- `/user` pide `USER` o `ADMIN`.
- `/admin` solo `ADMIN`.

Hay además `/login` (formulario Thymeleaf con CSRF), `/access-denied` (página
propia de denegación en lugar del 403 del contenedor) y `/` (landing con
tarjetas de navegación y botón de logout).

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

`PruebasSeguridadTest` cubre con MockMvc la matriz completa de permisos (3
rutas por 3 estados: anónimo, `USER`, `ADMIN`), más login, logout y denegación.

## Estructura

```
src/main/java/one/austral/lpa1/
  RappiClonApplication.java            entrada
  config/ConfiguracionSeguridad.java   SecurityFilterChain, reglas, login, logout
  security/ProveedorUsuariosEnMemoria.java   usuarios en memoria
  controller/ControladorPrincipal.java endpoints
src/main/resources/
  application.properties
  templates/                           login, access-denied, index, ruta
  static/css/                          base.css, app.css
src/test/java/one/austral/lpa1/
  PruebasSeguridadTest.java            MockMvc
```

Los paquetes van en inglés (`controller`, `config`, `security`) siguiendo la
convención de Spring. Las clases, métodos, variables y comentarios van en
español, que es el idioma del curso y del revisor.

## Documentación

- [PRD.md](./PRD.md) descripción del proyecto y de lo construido en U1.
- [docs/arquitectura-explicada.md](./docs/arquitectura-explicada.md) visión de
  la arquitectura.
- [docs/diagrams/](./docs/diagrams/) diagramas del sistema.
