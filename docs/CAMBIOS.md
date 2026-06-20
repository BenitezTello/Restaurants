# Documento de cambios

Registro de las funcionalidades agregadas/modificadas en esta iteración del
Sistema de Restaurantes (Tingo María). Código, UI y correos en español; sin
emojis; build con Docker; esquema solo por Flyway.

---

## 1. Asistente de reservas con IA (Gemini gratis)

**Qué hace:** el widget flotante "Asistente" responde preguntas en lenguaje
natural usando el contexto real de la reserva (estado, fecha/hora en formato
12h, adelanto, formas de pago y QR). Si no hay API key, cae al modo por reglas.

- **Proveedor:** Google Gemini (capa gratuita de Google AI Studio). Modelo
  `gemini-2.5-flash-lite` (el `gemini-2.0-flash` tiene cuota 0 en el proyecto).
  No se usa Anthropic (no tiene plan gratis).
- **Config:** `GEMINI_API_KEY` y `GEMINI_MODEL` (env). Si falta la key →
  `configured:false` y el front usa el modo por reglas.
- **Backend:**
  - `application/dto/request/AssistantChatRequest.java`
  - `application/service/AssistantService.java` (llama a Gemini por HTTP con `RestClient`; arma el system prompt con los datos de la reserva por código)
  - `infrastructure/web/controller/AssistantController.java` → `POST /v1/assistant/chat` (público; en `SecurityConfig` `permitAll`)
  - `application.yml` → bloque `gemini:`
- **Frontend:**
  - `services/assistantService.ts`
  - `components/ui/ReservationAssistant.tsx`: el código busca la reserva; "comprobante/alergias/pago" van por acción (el pago muestra datos reales + QR); el resto va a la IA. **Ya no repite el menú** en cada respuesta; hay una **barra fija de acciones rápidas** abajo. Botón flotante rediseñado.

## 2. Pago del adelanto: rechazo, apartado propio y "dónde pagar"

- **Botón "Rechazar"** un comprobante (además de Verificar):
  - Backend: `PaymentService.reject()` + `PATCH /v1/payments/{id}/reject`
    (marca el pago `REJECTED` y devuelve la reserva a `PENDING_PAYMENT`).
  - Frontend: `paymentService.reject()`.
- **Apartado propio "Pagos"** (`/dashboard/pagos`): salió del fondo de "Reglas
  de reserva". Muestra "Por verificar" (con contador) e historial. Responsive y
  modo claro/oscuro. Nuevo ítem en el sidebar (`payments`).
- **Dónde/cómo pagar** ahora se muestra de verdad:
  - Correo de **"reserva recibida"** incluye el bloque de pago (monto, concepto, formas de pago y QR) — `EmailService.sendReservationCreated` ahora usa `paymentBlock(r)`.
  - Página **/reservations**: el bloque "Adelanto" muestra `paymentInfo` + imagen del QR del restaurante (en vez de "revisa tu correo").
  - Asistente: la acción de pago muestra el QR real.

## 3. Registro y login mejorados (cuentas de restaurante con aprobación)

**Regla:** el registro por formulario es **solo para dueños de restaurante**.
Los clientes se registran/ingresan **solo con Google**.

**Flujo:** el dueño solicita su cuenta (datos del dueño + de al menos un
restaurante) → queda **en revisión** (no entra al panel, login bloqueado con
mensaje) → el **admin** revisa y **aprueba** (activa la cuenta y publica el
restaurante) o **rechaza** (con motivo) → correos automáticos en cada paso.

- **Estado de cuenta:** migración `V21__owner_account_status.sql`
  (`users.account_status`: `ACTIVE | PENDING_REVIEW | REJECTED`).
  Enum `domain/model/enums/AccountStatus.java`; mapeado en `User`, `UserEntity`,
  `UserRepositoryAdapter`.
- **Backend:**
  - `RegisterOwnerRequest` (datos del dueño + `CreateRestaurantRequest` anidado)
  - `AuthService.registerOwner()` (crea dueño `PENDING_REVIEW` + restaurante `PENDING_APPROVAL`, envía correo) y `login()` con mensajes de estado.
  - `AuthController` → `POST /v1/auth/register-owner` (público; sin token).
  - Excepción `AccountNotActiveException` → HTTP 403 (`GlobalExceptionHandler`).
  - Revisión admin: `RegistrationReviewService` + `AdminRegistrationController`
    (`GET /v1/admin/registration-requests`, `POST .../{userId}/approve`, `POST .../{userId}/reject`), solo `ADMIN`.
  - `UserJpaRepository.findByAccountStatusAndDeletedAtIsNullOrderByCreatedAtDesc`.
  - Correos: `EmailService.sendOwnerApplicationReceived/Approved/Rejected`.
- **Frontend:**
  - `app/(auth)/register/page.tsx`: asistente de **2 pasos** (Tu cuenta → Tu
    restaurante) + pantalla **"Solicitud recibida"** (sin auto-login). Mantiene
    la opción de cliente con Google.
  - `app/(auth)/login/page.tsx`: el enlace aclara que el registro es para
    restaurantes; los clientes usan Google.
  - `app/dashboard/solicitudes/page.tsx` + `services/adminRegistrationService.ts`
    + ítem de sidebar `registrationRequests` (solo admin).
  - `authService.registerOwner()`.

## 4. Onboarding (guías interactivas)

- Motor reutilizable `components/ui/SpotlightTour.tsx`.
  - La tarjeta se ubica **al lado** del elemento resaltado (ya no lo tapa) y es
    **responsive** (si el elemento no es visible, p. ej. sidebar en móvil,
    muestra la tarjeta centrada).
- Tour del **cliente** (`OnboardingTour.tsx`): primera vez en `/restaurants`
  (filtros, listado y asistente).
- Tour del **dueño** (`OwnerOnboardingTour.tsx`): primera vez en `/dashboard`
  (restaurante, menús, promociones, **reglas de reserva y pagos**, reservas,
  reportes). Items del sidebar marcados con `data-tour="nav-<clave>"`.

## 4.bis Ofertas con flyer (IA) en la página principal

- **Carrusel de ofertas** en `/restaurants`: muestra las promociones activas de
  todos los restaurantes como **flyers**, con avance por flechas ◀ ▶ (sin barra
  de scroll), responsive y dark mode (`components/ui/OffersCarousel.tsx`).
- **Flyer híbrido:** la **IA (Gemini texto, gratis)** genera el copy (titular +
  subtítulo) y el **sistema diseña** el flyer (`components/ui/PromoFlyer.tsx`).
  No se genera ninguna imagen-archivo; solo se guarda el copy en BD.
- **Botón "Generar flyer"** en el panel de Promociones (con vista previa); si la
  IA no está disponible, usa el título/descripción como fallback.
- **Backend:** migración `V22__promotion_flyer.sql` (`flyer_headline`,
  `flyer_tagline`), `GeminiTextClient`, `PromotionService.generateFlyer/showcase`,
  endpoints `POST /v1/promotions/{id}/flyer` (dueño) y `GET /v1/promotions/showcase`
  (público).

## 5. Otros ajustes

- Sidebar: más compacto; ya no muestra scroll cuando hay espacio suficiente.

- "Reglas de reserva": el botón **Guardar** se activa **solo si hay cambios**
  (estado *dirty*) con aviso "Tienes cambios sin guardar". El selector de
  restaurante usa el mismo `RestaurantPicker` que el resto de secciones.
- QR de pago del restaurante: el dueño sube su QR (Yape/Plin) en "Reglas de
  reserva" (Cloudinary, carpeta `payment-qr`); se muestra en asistente, correo
  y página de la reserva.

---

## Migraciones nuevas

| Versión | Archivo | Cambio |
|---|---|---|
| V21 | `V21__owner_account_status.sql` | `users.account_status` (default `ACTIVE`) |

> Nota: las migraciones previas relacionadas (V19 pagos, V20 QR de pago) ya
> estaban aplicadas.

## Variables de entorno nuevas

| Variable | Uso |
|---|---|
| `GEMINI_API_KEY` | API key de Google AI Studio (asistente IA). Vacía = modo reglas. |
| `GEMINI_MODEL` | Modelo de Gemini (por defecto `gemini-2.5-flash-lite`). |

## Endpoints nuevos

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/v1/assistant/chat` | público |
| POST | `/v1/auth/register-owner` | público |
| PATCH | `/v1/payments/{id}/reject` | ADMIN / RESTAURANTE_OWNER |
| GET | `/v1/admin/registration-requests` | ADMIN |
| POST | `/v1/admin/registration-requests/{userId}/approve` | ADMIN |
| POST | `/v1/admin/registration-requests/{userId}/reject` | ADMIN |

## Limitación conocida

- El regex de teléfono del backend exige 10–12 dígitos, por lo que un móvil
  peruano de 9 dígitos se rechaza. Por eso el teléfono es **opcional** en el
  registro. Pendiente: relajar el patrón a 9 dígitos si se desea.
