# RappiClon

App web del curso Lenguaje de Programación Avanzado I (Uniremington, 2026-2).
Dominio: catálogo de productos y pedidos, estilo e-commerce. Esta entrega
corresponde a la Unidad 3: **API RESTful con SPA en Svelte y persistencia en
PostgreSQL**, sobre la app de las unidades 1 (seguridad con Spring Security)
y 2 (internacionalización ES/EN/PT).

## Qué hace

RappiClon es una tienda de comida rápida con:

- **Catálogo de 19 productos** en 4 categorías (hamburguesas, comidas,
  bebidas y postres), cada uno con imagen, descripción y precio en pesos
  colombianos.
- **Registro e inicio de sesión** con correo y contraseña (JWT).
- **Recuperación de contraseña por correo**: la app envía un enlace con
  token de un solo uso que expira en 30 minutos.
- **Búsqueda** de productos por nombre o palabra clave.
- **Detalle** de producto con imagen, descripción, precio, unidad y stock.
- **Tres idiomas** (ES/EN/PT) seleccionables en la cabecera, en toda la SPA.
- **Roles**: `USER` y `ADMIN`; el panel de administración lista los usuarios
  registrados y solo es accesible con rol ADMIN.

## Arquitectura

```
SPA Svelte (Vite, :5173)  →  API Spring Boot (:8080)  →  PostgreSQL 17
        (frontend/)             (src/)                     (rappiclon)
```

- Backend REST con Spring Boot 4.1 (Java 21), Spring Security con **JWT
  stateless** (HS256), Spring Data JPA y Springdoc (Swagger UI en
  `/swagger-ui/index.html`).
- Frontend SPA con **Svelte 5 + Vite** (router por hash, i18n ES/EN/PT,
  token en `localStorage`).
- Persistencia en **PostgreSQL 17** (en desarrollo corre en un contenedor;
  en cualquier entorno basta un PostgreSQL estándar con las mismas
  credenciales).
- Las contraseñas se guardan hasheadas con **BCrypt**; los tokens de
  recuperación se guardan hasheados (SHA-256), expiran a los 30 minutos y
  son de un solo uso.

## Arrancar

### Opción A — Docker Compose (recomendada para revisar el proyecto)

```bash
docker compose up -d
```

Levanta PostgreSQL 17 y Mailpit (buzón local de correos) con las
credenciales que espera la app. Después:

```bash
./mvnw spring-boot:run          # API en :8080
cd frontend && npm install && npm run dev   # SPA en :5173
```

### Opción B — PostgreSQL/Mailpit existentes

1. **Base de datos**: PostgreSQL en `localhost:5432`, base `rappiclon`,
   usuario `rappi`, contraseña `rappi123`. Con cualquier PostgreSQL —o
   con `docker compose up -d`— se crean la base y el usuario con esos
   valores.
2. **Correo (opcional para la recuperación)**: Mailpit en `localhost:1025`
   (SMTP) con interfaz en `http://localhost:8025`. Sin Mailpit, la
   recuperación responde 200 pero el correo no se entrega.
3. **Backend**: `./mvnw spring-boot:run` (puerto 8080). La primera vez
   siembra el catálogo (19 productos) y los usuarios base.
4. **Frontend**: `cd frontend && npm install && npm run dev` (puerto 5173,
   con proxy a la API en 8080). Para producción: `npm run build`.

Credenciales base: `admin` / `admin123` (ADMIN) y `user` / `user123` (USER).
Los usuarios nuevos se registran desde la SPA (requiere un correo con
formato válido).

## API REST (resumen)

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/register` | público |
| POST | `/api/auth/login` | público |
| POST | `/api/auth/forgot` | público |
| POST | `/api/auth/reset` | público |
| GET | `/api/productos?categoria=` | público |
| GET | `/api/productos/{id}` | público |
| GET | `/api/productos/buscar?q=` | público |
| GET | `/api/categorias` | público |
| GET | `/api/admin/usuarios` | ADMIN |

Documentación interactiva: `/swagger-ui/index.html`.

## Pruebas

- **Backend** (unitarias e integración): `./mvnw test`. Requiere
  PostgreSQL corriendo (se usa la base `rappiclon_test`, creada con el
  mismo usuario `rappi`/`rappi123`).
- **End-to-end** (SPA + API, Playwright): con la API y la SPA corriendo,
  `cd e2e && npm install && npx playwright test`.

## Stack

- Java 21, Spring Boot 4.1, Spring Security 7.1 (JWT), Spring Data JPA
- Playwright (suite end-to-end en `e2e/`)
- Svelte 5.56 + Vite
- PostgreSQL 17
- Thymeleaf se mantiene solo para las vistas de la Unidad 1 (login,
  panel y páginas de error por formulario)
