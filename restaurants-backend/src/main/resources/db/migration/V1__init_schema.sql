-- ============================================================
-- SISTEMA DE RESTAURANTES - TINGO MARÍA
-- V1: Esquema inicial completo
-- ============================================================

-- Extensiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- PostGIS ya viene instalado en la imagen postgis/postgis

-- Los valores de enumeración se almacenan como VARCHAR para compatibilidad con Hibernate @Enumerated(EnumType.STRING)

-- ============================================================
-- TABLA: users
-- ============================================================

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    phone           VARCHAR(20),
    role            VARCHAR(50) NOT NULL DEFAULT 'CLIENTE',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_role ON users(role) WHERE deleted_at IS NULL;

-- ============================================================
-- TABLA: food_categories
-- ============================================================

CREATE TABLE food_categories (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_url    VARCHAR(500),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- TABLA: restaurants
-- ============================================================

CREATE TABLE restaurants (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id            UUID NOT NULL REFERENCES users(id),
    name                VARCHAR(200) NOT NULL,
    slug                VARCHAR(200) NOT NULL UNIQUE,
    description         TEXT,
    phone               VARCHAR(20),
    email               VARCHAR(255),
    website             VARCHAR(500),
    ruc                 VARCHAR(20),
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL',

    -- Ubicación
    address             VARCHAR(500) NOT NULL,
    district            VARCHAR(100),
    city                VARCHAR(100) NOT NULL DEFAULT 'Tingo María',
    region              VARCHAR(100) NOT NULL DEFAULT 'Huánuco',
    latitude            DECIMAL(10, 8),
    longitude           DECIMAL(11, 8),
    geolocation         GEOGRAPHY(POINT, 4326),

    -- Capacidad
    total_capacity      INT NOT NULL DEFAULT 0,
    min_reservation_size INT NOT NULL DEFAULT 1,
    max_reservation_size INT NOT NULL DEFAULT 20,

    -- Imágenes
    cover_image_url     VARCHAR(500),
    logo_url            VARCHAR(500),

    -- Calificación calculada
    avg_rating          DECIMAL(3, 2) DEFAULT 0,
    total_ratings       INT NOT NULL DEFAULT 0,

    -- Configuración
    accepts_reservations BOOLEAN NOT NULL DEFAULT TRUE,
    accepts_events       BOOLEAN NOT NULL DEFAULT FALSE,
    has_parking          BOOLEAN NOT NULL DEFAULT FALSE,
    has_wifi             BOOLEAN NOT NULL DEFAULT FALSE,
    has_air_conditioning BOOLEAN NOT NULL DEFAULT FALSE,
    is_accessible        BOOLEAN NOT NULL DEFAULT FALSE,

    -- Auditoría
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ
);

CREATE INDEX idx_restaurants_owner ON restaurants(owner_id);
CREATE INDEX idx_restaurants_status ON restaurants(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_restaurants_city ON restaurants(city) WHERE deleted_at IS NULL;
CREATE INDEX idx_restaurants_rating ON restaurants(avg_rating DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_restaurants_geo ON restaurants USING GIST(geolocation) WHERE deleted_at IS NULL;
CREATE INDEX idx_restaurants_slug ON restaurants(slug) WHERE deleted_at IS NULL;

-- ============================================================
-- TABLA: restaurant_food_categories (relación M:N)
-- ============================================================

CREATE TABLE restaurant_food_categories (
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    category_id     UUID NOT NULL REFERENCES food_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (restaurant_id, category_id)
);

-- ============================================================
-- TABLA: restaurant_tables
-- ============================================================

CREATE TABLE restaurant_tables (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    table_number    VARCHAR(20) NOT NULL,
    capacity        INT NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    location_desc   VARCHAR(200),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (restaurant_id, table_number)
);

CREATE INDEX idx_tables_restaurant ON restaurant_tables(restaurant_id);
CREATE INDEX idx_tables_status ON restaurant_tables(status);

-- ============================================================
-- TABLA: schedules (horarios de apertura)
-- ============================================================

CREATE TABLE schedules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    day_of_week     VARCHAR(20) NOT NULL,
    opening_time    TIME NOT NULL,
    closing_time    TIME NOT NULL,
    is_closed       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (restaurant_id, day_of_week)
);

CREATE INDEX idx_schedules_restaurant ON schedules(restaurant_id);

-- ============================================================
-- TABLA: menus
-- ============================================================

CREATE TABLE menus (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    valid_from      DATE,
    valid_until     DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_menus_restaurant ON menus(restaurant_id) WHERE deleted_at IS NULL;

-- ============================================================
-- TABLA: dishes
-- ============================================================

CREATE TABLE dishes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    menu_id         UUID NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id),
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    category        VARCHAR(50) NOT NULL,
    price           DECIMAL(10, 2) NOT NULL,
    preparation_time INT,
    calories        INT,
    image_url       VARCHAR(500),
    is_available    BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured     BOOLEAN NOT NULL DEFAULT FALSE,
    is_vegetarian   BOOLEAN NOT NULL DEFAULT FALSE,
    is_vegan        BOOLEAN NOT NULL DEFAULT FALSE,
    is_gluten_free  BOOLEAN NOT NULL DEFAULT FALSE,
    allergens       TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_dishes_menu ON dishes(menu_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_dishes_restaurant ON dishes(restaurant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_dishes_category ON dishes(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_dishes_available ON dishes(is_available, is_featured) WHERE deleted_at IS NULL;

-- ============================================================
-- TABLA: promotions
-- ============================================================

CREATE TABLE promotions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    promo_type      VARCHAR(50) NOT NULL,
    discount_value  DECIMAL(10, 2),
    min_order_amount DECIMAL(10, 2),
    promo_code      VARCHAR(50),
    image_url       VARCHAR(500),
    valid_from      TIMESTAMPTZ NOT NULL,
    valid_until     TIMESTAMPTZ NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    usage_limit     INT,
    usage_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT valid_promotion_dates CHECK (valid_until > valid_from),
    CONSTRAINT positive_discount CHECK (discount_value IS NULL OR discount_value > 0)
);

CREATE INDEX idx_promotions_restaurant ON promotions(restaurant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_promotions_active ON promotions(is_active, valid_from, valid_until) WHERE deleted_at IS NULL;

-- ============================================================
-- TABLA: reservations
-- ============================================================

CREATE TABLE reservations (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id       UUID NOT NULL REFERENCES restaurants(id),
    customer_id         UUID REFERENCES users(id),
    table_id            UUID REFERENCES restaurant_tables(id),

    -- Datos del cliente (puede ser sin cuenta)
    customer_name       VARCHAR(150) NOT NULL,
    customer_email      VARCHAR(255),
    customer_phone      VARCHAR(20) NOT NULL,

    -- Detalles de reserva
    reservation_date    DATE NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME,
    party_size          INT NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes               TEXT,
    special_requests    TEXT,

    -- Código de confirmación
    confirmation_code   VARCHAR(20) NOT NULL UNIQUE,

    -- Vinculación con eventos externos
    related_event_id    UUID,
    related_event_name  VARCHAR(200),
    is_event_related    BOOLEAN NOT NULL DEFAULT FALSE,

    -- Auditoría
    confirmed_at        TIMESTAMPTZ,
    cancelled_at        TIMESTAMPTZ,
    cancellation_reason TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT positive_party_size CHECK (party_size > 0)
);

CREATE INDEX idx_reservations_restaurant ON reservations(restaurant_id);
CREATE INDEX idx_reservations_customer ON reservations(customer_id);
CREATE INDEX idx_reservations_date ON reservations(reservation_date, start_time);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_confirmation ON reservations(confirmation_code);
CREATE INDEX idx_reservations_event ON reservations(related_event_id) WHERE is_event_related = TRUE;

-- ============================================================
-- TABLA: ratings
-- ============================================================

CREATE TABLE ratings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    user_id         UUID REFERENCES users(id),
    reservation_id  UUID REFERENCES reservations(id),
    score           INT NOT NULL,
    comment         TEXT,
    food_score      INT,
    service_score   INT,
    ambiance_score  INT,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT valid_food_score CHECK (food_score IS NULL OR food_score BETWEEN 1 AND 5),
    CONSTRAINT valid_service_score CHECK (service_score IS NULL OR service_score BETWEEN 1 AND 5),
    CONSTRAINT valid_ambiance_score CHECK (ambiance_score IS NULL OR ambiance_score BETWEEN 1 AND 5)
);

CREATE INDEX idx_ratings_restaurant ON ratings(restaurant_id);
CREATE INDEX idx_ratings_user ON ratings(user_id);
CREATE UNIQUE INDEX idx_ratings_reservation ON ratings(reservation_id) WHERE reservation_id IS NOT NULL;

-- ============================================================
-- TABLA: related_events (eventos del ecosistema)
-- ============================================================

CREATE TABLE related_events (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    external_event_id   VARCHAR(100) NOT NULL,
    event_name          VARCHAR(300) NOT NULL,
    event_date          DATE NOT NULL,
    event_location      VARCHAR(500),
    event_latitude      DECIMAL(10, 8),
    event_longitude     DECIMAL(11, 8),
    expected_attendance INT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_related_events_date ON related_events(event_date);
CREATE UNIQUE INDEX idx_related_events_external ON related_events(external_event_id);

-- ============================================================
-- TABLA: refresh_tokens
-- ============================================================

CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(500) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    is_revoked  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- ============================================================
-- FUNCIÓN: Actualizar avg_rating automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION update_restaurant_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE restaurants
    SET avg_rating = (
        SELECT ROUND(AVG(score)::NUMERIC, 2)
        FROM ratings
        WHERE restaurant_id = NEW.restaurant_id
    ),
    total_ratings = (
        SELECT COUNT(*)
        FROM ratings
        WHERE restaurant_id = NEW.restaurant_id
    ),
    updated_at = NOW()
    WHERE id = NEW.restaurant_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_restaurant_rating
AFTER INSERT OR UPDATE ON ratings
FOR EACH ROW EXECUTE FUNCTION update_restaurant_rating();

-- ============================================================
-- FUNCIÓN: Actualizar updated_at automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_restaurants_updated_at BEFORE UPDATE ON restaurants FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_menus_updated_at BEFORE UPDATE ON menus FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_dishes_updated_at BEFORE UPDATE ON dishes FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_reservations_updated_at BEFORE UPDATE ON reservations FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_promotions_updated_at BEFORE UPDATE ON promotions FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER trg_schedules_updated_at BEFORE UPDATE ON schedules FOR EACH ROW EXECUTE FUNCTION update_updated_at();
