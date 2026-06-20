# Manual de pruebas

Guía paso a paso para verificar las funcionalidades de esta iteración.

## Preparación

```bash
docker compose up -d            # levanta todo el stack
# o tras cambios:
docker compose up --build backend -d
docker compose up --build frontend -d
```

- Frontend: http://localhost:3000 · API: http://localhost:8080/api · Swagger:
  http://localhost:8080/api/swagger-ui.html
- Para la IA del asistente: define `GEMINI_API_KEY` en `.env`
  (https://aistudio.google.com/apikey) y reconstruye el backend.

**Credenciales (seed):**

| Rol | Email | Password |
|---|---|---|
| Admin | `admin@tingo-restaurants.com` | `Admin@1234!` |
| Dueño | `owner.<nombre>@tingomaria.com` | `Admin@1234!` |

Tip: para repetir un onboarding, en la consola del navegador:
`localStorage.removeItem('onboarding_seen_<userId>')` (cliente) o
`localStorage.removeItem('owner_onboarding_seen_<userId>')` (dueño).

---

## 1. Registro de cuenta de restaurante (con aprobación del admin)

1. Ir a `/register`. Verificar que el formulario es de **2 pasos**:
   - Paso 1 "Tu cuenta": nombre, correo, teléfono (opcional), contraseña + confirmación. La contraseña exige mayúscula, minúscula, número y símbolo.
   - Paso 2 "Tu restaurante": nombre, descripción, dirección, distrito, ciudad, región, teléfono/RUC (opcionales), capacidad, nivel de precio.
2. Enviar la solicitud → debe aparecer la pantalla **"¡Solicitud recibida!"**
   (NO debe iniciar sesión ni entrar al dashboard).
3. (Si hay correo configurado) llega el correo "Recibimos tu solicitud".
4. Intentar **iniciar sesión** con esa cuenta en `/login` → debe **bloquear**
   con el mensaje "Tu solicitud de cuenta está en revisión…" (HTTP 403).
5. Entrar como **admin** → menú **Solicitudes** (`/dashboard/solicitudes`):
   - Se ve la solicitud con datos del dueño y del restaurante.
   - **Aprobar y publicar** → toast de éxito; la solicitud desaparece.
6. Iniciar sesión con la cuenta del dueño → ahora **sí entra** al panel.
   - Verificar que el restaurante aparece **público** en `/restaurants`.
7. Repetir creando otra solicitud y probar **Rechazar** (con motivo):
   - El dueño no puede entrar; al loguear ve "Tu solicitud… fue rechazada".
   - (Si hay correo) llega el correo de rechazo con el motivo.

**Login (UI):** en `/login`, el enlace inferior debe decir que el registro es
para restaurantes y que los clientes usan Google.

## 2. Cliente con Google + onboarding del cliente

1. En `/register` o `/login`, usar **Google** (rol CLIENTE).
   > Requiere configurar en Google Cloud el origen `http://localhost:3000`.
2. Tras el primer login, en `/restaurants` debe aparecer el **tour del cliente**
   (bienvenida → filtros → listado → asistente).
3. Verificar que el tooltip **no tapa** el elemento resaltado y que **Saltar**
   / **Siguiente** / **Anterior** funcionan.

## 3. Onboarding del dueño

1. Con un dueño **recién aprobado**, entrar a `/dashboard`.
2. Debe aparecer el **tour del dueño** recorriendo el sidebar (restaurante,
   menús, promociones, **reglas de reserva y pagos**, reservas, reportes).
3. La tarjeta debe ubicarse **al lado** de cada ítem (sin taparlo).

## 4. Responsive y modo claro/oscuro

1. Reducir la ventana a ancho de móvil. En el onboarding, si el sidebar está
   oculto, la tarjeta debe mostrarse **centrada y legible** (no un spotlight
   perdido).
2. Alternar tema claro/oscuro y revisar: registro, login, Pagos, Solicitudes,
   Reglas de reserva y el asistente.

## 5. Reglas de reserva: botón Guardar (estado dirty)

1. Entrar a `/dashboard/reservas-config` (Reglas de reserva).
2. Sin tocar nada, el botón **Guardar** debe estar **deshabilitado**.
3. Cambiar cualquier campo → el botón se **habilita** y aparece "Tienes cambios
   sin guardar". Guardar → vuelve a deshabilitarse.
4. El selector de restaurante usa tarjetas (`RestaurantPicker`), igual que en
   Menús/Promociones.

## 6. Pago del adelanto

Precondición: un restaurante con "Exigir adelanto" activado y, opcionalmente,
`paymentInfo` (formas de pago) y QR cargado en Reglas de reserva.

1. Como cliente, crear una reserva que requiera adelanto.
2. **Correo "reserva recibida"**: debe incluir monto, concepto, formas de pago
   y el QR (si lo cargó el restaurante).
3. Página `/reservations` (buscar por código): el bloque **Adelanto** muestra
   `paymentInfo` + imagen del QR (no "revisa tu correo"). Subir comprobante.
4. Como dueño, ir a **Pagos** (`/dashboard/pagos`):
   - En "Por verificar" aparece el comprobante (con contador).
   - **Verificar** → pasa a historial como "Verificado".
   - **Rechazar** (otro pago) → "Rechazado"; la reserva vuelve a "pendiente de
     pago" para reintentar.

## 7. Asistente con IA

Precondición: `GEMINI_API_KEY` configurada (si no, valida el modo por reglas).

1. Abrir el botón **Asistente** (abajo a la derecha; no aparece en `/dashboard`).
2. Escribir el **código** de una reserva (ej. `RES-XXXXXXXX`) → carga la reserva.
3. Preguntar en lenguaje natural, p. ej. "¿a qué hora es mi reserva?" → la IA
   responde con datos reales (hora en formato 12h, personas, etc.).
4. Preguntar "¿cómo/dónde pago?" → muestra el monto, las **formas de pago** y el
   **QR** real del restaurante.
5. Verificar que **no** se repite el menú largo en cada respuesta y que la
   **barra de acciones rápidas** (Estado, Pago, Comprobante, Alergias, Otra
   reserva) está siempre disponible abajo.
6. "Subir comprobante" desde el asistente (requiere sesión Google del cliente).

## Pruebas rápidas por API (curl)

```bash
# Registro de dueño (queda pendiente)
curl -s -X POST http://localhost:8080/api/v1/auth/register-owner \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Prueba","email":"p@x.com","password":"Admin@1234!","restaurant":{"name":"R","address":"Av 1","city":"Tingo Maria","region":"Huanuco","totalCapacity":40}}'

# Login pendiente -> 403 ACCOUNT_PENDING
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" -d '{"email":"p@x.com","password":"Admin@1234!"}'

# Asistente (sin key -> {"configured":false})
curl -s -X POST http://localhost:8080/api/v1/assistant/chat \
  -H "Content-Type: application/json" -d '{"messages":[{"role":"user","content":"hola"}]}'
```
