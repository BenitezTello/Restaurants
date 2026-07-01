# Batería de casos de prueba — RFt-1-G · Evitación de fallos (ISO/IEC 25023)

**Característica:** Fiabilidad → Tolerancia a fallos
**Métrica:** RFt-1-G · Evitación de fallos
**Fórmula:** `X = A / B`

- **A** = número de patrones de fallo que el sistema **controló** (respondió de forma
  controlada, con envelope `ApiResponse` y código de estado esperado, sin caerse ni
  filtrar stacktraces).
- **B** = número de casos de prueba de fallo **ejecutados**.

> Interpretación: `X` cercano a **1.0** = el sistema evita/gestiona bien los fallos.

## Criterio de "controlado" (cuándo un caso suma a `A`)

Alineado con la definición ISO de RFt-1: *"patrones de fallo controlados para evitar
fallos críticos y serios"*. Un caso está **CONTROLADO** si el sistema respondió **sin
un fallo serio/crítico**, es decir:

- El sistema **respondió** (no hubo cuelgue/timeout, `status != 0`), y
- **no devolvió un 5xx** (`status < 500`). Un `4xx` es el sistema rechazando
  correctamente la entrada inválida → fallo evitado. Un `5xx` es un fallo serio que
  el sistema no supo gestionar → fallo NO evitado.

Además se reportan dos indicadores de **calidad** (no afectan la métrica RFt-1, pero
sirven para el análisis):

- **Semántica**: si el código HTTP coincide exactamente con el esperado (p. ej. que un
  JSON malformado devuelva `400` y no `500`).
- **Envelope**: si la respuesta viene con el sobre `ApiResponse` gestionado por
  `GlobalExceptionHandler`.

> Caso especial RFt1-12 (fuerza bruta): se considera controlado solo si el sistema
> **defiende** el patrón devolviendo `429` (rate limiting); un `401` repetido sin
> bloqueo se marca NO controlado.

## Matriz de casos

Base API: `http://localhost:8080/api` · Todos contra el stack `docker compose up -d`.

| ID | Patrón de fallo | Petición | HTTP esperado | Handler que lo gestiona |
|----|-----------------|----------|---------------|-------------------------|
| RFt1-01 | Recurso inexistente | `GET /v1/restaurants/{uuid-aleatorio}` | 404 | `RestaurantNotFoundException` |
| RFt1-02 | Identificador malformado | `GET /v1/restaurants/no-es-uuid` | 400 | Spring type-mismatch |
| RFt1-03 | Cuerpo vacío / campos faltantes | `POST /v1/auth/login` body `{}` | 400 | `MethodArgumentNotValidException` |
| RFt1-04 | Formato de dato inválido | `POST /v1/auth/login` email `"abc"` | 400 | `MethodArgumentNotValidException` |
| RFt1-05 | Credenciales incorrectas | `POST /v1/auth/login` password errónea | 401 | `InvalidCredentialsException` |
| RFt1-06 | Acceso sin autenticación | `POST /v1/restaurants` sin token | 401 / 403 | Spring Security |
| RFt1-07 | Token inválido / corrupto | `POST /v1/restaurants` con Bearer basura | 401 / 403 | Spring Security |
| RFt1-08 | Autorización insuficiente (rol) | `POST /v1/restaurants` con token CLIENTE | 403 | `AccessDeniedException` |
| RFt1-09 | JSON malformado | `POST /v1/auth/login` body `{"email":` | 400 | `HttpMessageNotReadable` |
| RFt1-10 | Método HTTP no permitido | `DELETE /v1/auth/login` | 405 | Spring MVC |
| RFt1-11 | Ruta inexistente | `GET /v1/ruta-que-no-existe` | 404 | Spring MVC |
| RFt1-12 | Fuerza bruta / rate limiting | 10× `POST /v1/auth/login` password errónea | 429 (en algún intento) | `TooManyAttemptsException` |
| RFt1-13 | Dependencia caída (BD) | Parar Postgres → `GET /v1/restaurants` | 5xx **controlado** (envelope, sin colgarse) | `Exception` genérico |

> RFt1-13 se ejecuta aparte con `run-caos-bd.ps1` porque **detiene el contenedor de
> Postgres** (prueba de caos). Los casos RFt1-01..12 los ejecuta `run-rft1.ps1`.

## Cómo ejecutar

```powershell
# 1. Levantar el stack
docker compose up -d

# 2. Casos funcionales (01–12)
./iso-25023/tolerancia-fallos/run-rft1.ps1

# 3. Caso de caos de BD (13) — detiene y reinicia Postgres
./iso-25023/tolerancia-fallos/run-caos-bd.ps1
```

Cada corrida escribe un CSV y un informe `.md` con el valor final de **X = A/B** en
`iso-25023/tolerancia-fallos/resultados/`.

## Plantilla de resultados (rellenar tras ejecutar)

| ID | HTTP obtenido | Envelope | Controlado |
|----|---------------|----------|------------|
| RFt1-01 | | | |
| RFt1-02 | | | |
| ... | | | |

- **A (controlados):** ____
- **B (ejecutados):** ____
- **RFt1-1-G = A/B = ____**
