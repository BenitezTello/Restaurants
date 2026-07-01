# Tolerancia a fallos — Batería RFt-1-G (ISO/IEC 25023 · 25010)

Batería de pruebas para medir **RFt-1-G · Evitación de fallos** de la característica
*Fiabilidad → Tolerancia a fallos*.

## Contenido

| Archivo | Qué hace |
|---------|----------|
| `casos-prueba.md` | Documentación de los 13 casos, criterio de "controlado" y plantilla de resultados |
| `run-rft1.ps1` | Ejecuta los casos **01–12** contra la API y calcula `X = A/B` |
| `run-caos-bd.ps1` | Ejecuta el caso **13** (caos): detiene Postgres y verifica respuesta controlada |
| `Restaurants-RFt1.postman_collection.json` | Misma batería en Postman/Newman (portable) |
| `resultados/` | Se generan aquí los CSV e informes `.md` de cada corrida |

## Requisitos

- Stack levantado: `docker compose up -d` (API en `http://localhost:8080/api`).
- PowerShell 5.1+ (viene con Windows). No requiere dependencias externas.
- Para el caso de caos: Docker CLI accesible (usa `docker stop/start`).

## Ejecución rápida

```powershell
docker compose up -d

# Casos 01–12  (no destructivos)
./iso-25023/tolerancia-fallos/run-rft1.ps1

# Caso 13  (DETIENE y reinicia Postgres — solo local)
./iso-25023/tolerancia-fallos/run-caos-bd.ps1
```

Salida esperada (ejemplo):

```
=== RFt-1-G · Evitacion de fallos ===
[SI] RFt1-01 Recurso inexistente -> 404 (esperado 404, envelope=True)
[SI] RFt1-03 Cuerpo vacio -> 400 (esperado 400, envelope=True)
...
  A (controlados) = 12
  B (ejecutados)  = 12
  RFt-1-G = A/B   = 1
```

## Cómo se calcula la métrica

- **A** = casos en los que el sistema respondió de forma *controlada* (código HTTP
  esperado + envelope `ApiResponse`, sin caídas ni stacktraces).
- **B** = casos ejecutados.
- **RFt-1-G = A / B** → cuanto más cerca de `1.0`, mejor evita el sistema los fallos.

Los casos omitidos (por falta de prerequisito, p. ej. no poder crear el CLIENTE) se
marcan `OMITIDO` y **no cuentan** en `B`.

## Alternativa con Postman / Newman

```bash
newman run iso-25023/tolerancia-fallos/Restaurants-RFt1.postman_collection.json \
  --env-var baseUrl=http://localhost:8080/api
```

## Notas de interpretación para el informe

- `RFt1-13` (BD caída) evalúa **degradación controlada**: importa que la API devuelva
  un 5xx *gestionado* y no que se cuelgue.
- Si `RFt1-12` no arroja 429, significa que **no hay rate limiting** para ese patrón
  → es un hallazgo válido (fallo NO evitado), documéntalo como mejora.
- Esta carpeta vive dentro de `iso-25023/`, junto a las baterías de las otras
  características (disponibilidad, interoperabilidad).
