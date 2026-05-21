Actúa como un arquitecto de software senior, arquitecto cloud y líder técnico especializado en:

- Java
- Spring Boot
- PostgreSQL
- Microservicios
- Arquitectura Hexagonal
- Clean Architecture
- Docker
- Seguridad JWT
- Frontend moderno desacoplado
- Diseño UI/UX empresarial

Quiero construir un SISTEMA INDEPENDIENTE DE RESTAURANTES dentro de un ecosistema turístico y de gestión de eventos.

====================================================
CONTEXTO DEL NEGOCIO
====================================================

El sistema será usado en una plataforma turística y de eventos para localidades como Tingo María, donde existen:

- eventos culturales
- aniversarios
- fiestas locales
- turismo
- hoteles
- transporte
- restaurantes

IMPORTANTE:

- El sistema de restaurantes NO es un módulo del sistema de eventos.
- Es un sistema autónomo e independiente.
- Tiene su propia lógica de negocio.
- Tiene su propia base de datos.
- Expone APIs y servicios que pueden ser consumidos por:
  - Sistema de Gestión de Eventos
  - Sistema de Hoteles
  - Sistema de Lugares Turísticos
  - Sistema de Transporte

La idea es que el sistema de restaurantes pueda:

- ofrecer restaurantes cercanos a eventos
- mostrar disponibilidad
- gestionar reservas
- mostrar menús
- mostrar promociones
- mostrar horarios
- manejar capacidad del restaurante
- permitir atención para eventos masivos
- exponer información geográfica
- recomendar restaurantes según ubicación y demanda

====================================================
STACK TECNOLÓGICO BACKEND
====================================================

Backend:
- Java 21
- Spring Boot 3
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Maven
- PostgreSQL
- Docker
- Swagger/OpenAPI
- Jakarta Validation
- Lombok
- MapStruct
- Flyway o Liquibase
- SLF4J + Logback

Arquitectura:
- Microservicio independiente
- Arquitectura Hexagonal o Clean Architecture
- APIs REST
- Preparado para integración futura con RabbitMQ o Kafka

====================================================
STACK TECNOLÓGICO FRONTEND
====================================================

IMPORTANTE:
El frontend debe estar COMPLETAMENTE SEPARADO del backend.

Quiero arquitectura Frontend + Backend desacoplada.

Frontend:
- React o Next.js
- TypeScript
- TailwindCSS
- Shadcn/UI o Material UI
- Axios
- React Query / TanStack Query
- Zustand o Redux Toolkit
- React Hook Form
- Zod Validation

Diseño:
- Responsive
- Mobile First
- UI empresarial moderna
- Dashboard administrativo
- Modo oscuro
- UX profesional
- Buenas prácticas UI/UX
- Componentización
- Accesibilidad
- Diseño limpio tipo SaaS

====================================================
OBJETIVO GENERAL
====================================================

Quiero que el sistema se sienta como un proyecto empresarial real y NO como un CRUD básico.

Debe ser:
- escalable
- limpio
- desacoplado
- mantenible
- modular
- profesional
- preparado para producción

====================================================
NECESITO QUE GENERES
====================================================

1. ARQUITECTURA COMPLETA DEL SISTEMA
- arquitectura backend
- arquitectura frontend
- separación frontend/backend
- capas
- responsabilidades
- flujo de datos
- patrones usados
- buenas prácticas

2. ESTRUCTURA PROFESIONAL DE CARPETAS
Backend:
- domain
- application
- infrastructure
- config
- security
- repository
- mapper
- dto
- controller
- exception
- integration
- event
- validation
- util
- audit

Frontend:
- app
- pages
- routes
- components
- layouts
- features
- hooks
- services
- store
- styles
- utils
- validations
- types

3. DISEÑO DE BASE DE DATOS POSTGRESQL
Incluyendo:
- tablas
- relaciones
- constraints
- índices
- claves foráneas
- optimización
- auditoría
- soft delete

Entidades:
- Restaurante
- CategoríaComida
- Menú
- Plato
- Reserva
- Mesa
- Horario
- Promoción
- Ubicación
- Calificación
- Disponibilidad
- EventoRelacionado

4. APIs REST COMPLETAS
Con:
- endpoints
- métodos HTTP
- request/response
- paginación
- filtros
- autenticación JWT
- ejemplos JSON
- códigos HTTP

5. SEGURIDAD COMPLETA
- login
- registro
- refresh token
- roles
- permisos
- autenticación JWT
- autorización
- filtros de seguridad
- manejo de sesiones
- protección de rutas

Roles:
- ADMIN
- RESTAURANTE_OWNER
- CLIENTE
- SYSTEM_INTEGRATION

6. FUNCIONALIDADES AVANZADAS
- búsqueda geográfica
- restaurantes cercanos a eventos
- ranking por calificación
- filtros por horario
- filtros por capacidad
- promociones activas
- disponibilidad en tiempo real
- reservas inteligentes
- manejo de alta demanda
- recomendación de restaurantes

7. INTEGRACIÓN ENTRE SISTEMAS
Explica:
- qué expone el sistema
- qué consume
- cómo se integra con eventos
- cómo se integra con hoteles
- cómo se integra con turismo
- cómo se integra con transporte

Quiero:
- desacoplamiento
- APIs REST
- arquitectura preparada para eventos asíncronos

8. FRONTEND EMPRESARIAL
Genera:
- arquitectura frontend
- diseño responsive
- diseño mobile-first
- dashboard administrativo
- panel de reservas
- panel de restaurantes
- mapa interactivo
- UI moderna
- buenas prácticas UI/UX
- manejo global de estado
- consumo de APIs
- autenticación JWT frontend

9. UI/UX SKILLS Y BUENAS PRÁCTICAS
Quiero recomendaciones profesionales sobre:
- diseño visual
- tipografía
- espaciado
- diseño responsive
- accesibilidad
- paleta de colores
- experiencia de usuario
- navegación
- tablas
- formularios
- dashboards
- loaders
- skeletons
- feedback visual
- manejo de errores
- diseño moderno tipo SaaS

10. DOCKER Y DESPLIEGUE
Genera:
- Dockerfile backend
- Dockerfile frontend
- docker-compose
- configuración PostgreSQL
- variables de entorno
- configuración de producción
- perfiles dev/prod

11. ARCHIVOS IMPORTANTES
Genera:
- application.yml
- pom.xml
- docker-compose.yml
- .env.example
- Swagger config
- Security config
- CORS config

12. OBSERVABILIDAD Y CALIDAD
Incluye:
- logs
- auditoría
- manejo global de excepciones
- validaciones
- métricas
- monitoreo
- buenas prácticas empresariales

13. PATRONES Y BUENAS PRÁCTICAS
Quiero:
- SOLID
- Clean Code
- DTO Pattern
- Repository Pattern
- Service Pattern
- Specification Pattern
- Strategy Pattern
- Builder Pattern
- CQRS (opcional)
- Event Driven Ready

14. ROADMAP COMPLETO
Fase 1:
- Core del sistema

Fase 2:
- Reservas y disponibilidad

Fase 3:
- Integración con eventos

Fase 4:
- Integración con hoteles/turismo

Fase 5:
- Seguridad y optimización

Fase 6:
- Escalabilidad y eventos asíncronos

15. EXTRA IMPORTANTE
Quiero que:
- expliques decisiones arquitectónicas
- justifiques tecnologías
- propongas mejoras futuras
- propongas escalabilidad horizontal
- propongas cache con Redis
- propongas API Gateway futuro
- propongas integración futura con Kafka o RabbitMQ

====================================================
RESULTADO ESPERADO
====================================================

Quiero un diseño profesional empresarial completo, moderno y realista, listo para:
- exposición universitaria
- portafolio profesional
- implementación real
- escalabilidad futura

NO quiero un CRUD simple.
Quiero arquitectura seria y profesional.
