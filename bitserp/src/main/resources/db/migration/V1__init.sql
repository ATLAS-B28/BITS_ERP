-- ═══════════════════════════════════════════
--  V1__init.sql — BITSERP base schema
-- ═══════════════════════════════════════════

-- -- Extensions
-- CREATE EXTENSION IF NOT EXISTS postgis;
-- CREATE EXTENSION IF NOT EXISTS vector;
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ───── ROLES ─────
CREATE TABLE roles (
                       id      SERIAL PRIMARY KEY,
                       name    VARCHAR(50) UNIQUE NOT NULL
    -- ADMIN, INV_MANAGER, INV_EMPLOYEE,
    -- PROC_MANAGER, PROC_EMPLOYEE,
    -- SALES_MANAGER, SALES_EMPLOYEE,
    -- FIN_MANAGER, FIN_EMPLOYEE,
    -- VENDOR, LOGISTICS_PARTNER, CUSTOMER
);

-- ───── USERS ─────
CREATE TABLE users (
                       id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name            VARCHAR(100) NOT NULL,
                       email           VARCHAR(150) UNIQUE NOT NULL,
                       password_hash   TEXT NOT NULL,
                       role_id         INT NOT NULL REFERENCES roles(id),
                       active          BOOLEAN DEFAULT TRUE,
                       created_at      TIMESTAMPTZ DEFAULT NOW(),
                       updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── LOCATIONS (warehouses, vendor sites, stores) ─────
CREATE TABLE locations (
                           id              SERIAL PRIMARY KEY,
                           name            VARCHAR(100) NOT NULL,
                           type            VARCHAR(30) NOT NULL
                               CHECK (type IN ('warehouse','vendor_site','store','customer_site')),
                           address         TEXT,
                           city            VARCHAR(80),
                           state           VARCHAR(80),
                           country         VARCHAR(80) DEFAULT 'India',
                           coordinates     GEOGRAPHY(POINT, 4326),
                           active          BOOLEAN DEFAULT TRUE,
                           created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── PRODUCTS ─────
CREATE TABLE products (
                          id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          sku             VARCHAR(50) UNIQUE NOT NULL,
                          name            VARCHAR(150) NOT NULL,
                          category        VARCHAR(80),
                          unit_price      NUMERIC(12,2) NOT NULL,
                          unit_of_measure VARCHAR(20) DEFAULT 'units',
                          description     TEXT,
                          embedding       VECTOR(1536),
                          active          BOOLEAN DEFAULT TRUE,
                          created_at      TIMESTAMPTZ DEFAULT NOW(),
                          updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── INVENTORY ─────
CREATE TABLE inventory (
                           id              SERIAL PRIMARY KEY,
                           product_id      UUID NOT NULL REFERENCES products(id),
                           location_id     INT NOT NULL REFERENCES locations(id),
                           quantity        INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
                           reorder_level   INT NOT NULL DEFAULT 10,
                           updated_at      TIMESTAMPTZ DEFAULT NOW(),
                           UNIQUE(product_id, location_id)
);

-- ───── STOCK MOVEMENTS ─────
CREATE TABLE stock_movements (
                                 id              SERIAL PRIMARY KEY,
                                 product_id      UUID NOT NULL REFERENCES products(id),
                                 location_id     INT NOT NULL REFERENCES locations(id),
                                 change_qty      INT NOT NULL,
                                 reason          VARCHAR(50) NOT NULL
                                     CHECK (reason IN ('purchase_order','sale','adjustment','transfer','return')),
                                 reference_id    UUID,
                                 moved_by        UUID REFERENCES users(id),
                                 moved_at        TIMESTAMPTZ DEFAULT NOW()
);

-- ───── VENDORS ─────
CREATE TABLE vendors (
                         id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         name            VARCHAR(150) NOT NULL,
                         contact_email   VARCHAR(150),
                         contact_phone   VARCHAR(20),
                         location_id     INT REFERENCES locations(id),
                         user_id         UUID REFERENCES users(id),
                         active          BOOLEAN DEFAULT TRUE,
                         created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── PURCHASE ORDERS ─────
CREATE TABLE purchase_orders (
                                 id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 vendor_id       UUID NOT NULL REFERENCES vendors(id),
                                 status          VARCHAR(30) NOT NULL DEFAULT 'DRAFT'
                                     CHECK (status IN ('DRAFT','SUBMITTED','APPROVED','REJECTED','RECEIVED')),
                                 total_amount    NUMERIC(14,2),
                                 raised_by       UUID REFERENCES users(id),
                                 approved_by     UUID REFERENCES users(id),
                                 created_at      TIMESTAMPTZ DEFAULT NOW(),
                                 updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE purchase_order_items (
                                      id              SERIAL PRIMARY KEY,
                                      po_id           UUID NOT NULL REFERENCES purchase_orders(id),
                                      product_id      UUID NOT NULL REFERENCES products(id),
                                      quantity        INT NOT NULL CHECK (quantity > 0),
                                      unit_price      NUMERIC(12,2) NOT NULL,
                                      total_price     NUMERIC(14,2) GENERATED ALWAYS AS (quantity * unit_price) STORED
);

-- ───── CUSTOMERS ─────
CREATE TABLE customers (
                           id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name            VARCHAR(150) NOT NULL,
                           email           VARCHAR(150) UNIQUE,
                           phone           VARCHAR(20),
                           location_id     INT REFERENCES locations(id),
                           user_id         UUID REFERENCES users(id),
                           created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── SALES ORDERS ─────
CREATE TABLE sales_orders (
                              id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              customer_id     UUID NOT NULL REFERENCES customers(id),
                              status          VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                                  CHECK (status IN ('PENDING','CONFIRMED','DISPATCHED','DELIVERED','CANCELLED')),
                              delivery_address TEXT,
                              delivery_coords  GEOGRAPHY(POINT, 4326),
                              total_amount    NUMERIC(14,2),
                              created_by      UUID REFERENCES users(id),
                              created_at      TIMESTAMPTZ DEFAULT NOW(),
                              updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE sales_order_items (
                                   id              SERIAL PRIMARY KEY,
                                   order_id        UUID NOT NULL REFERENCES sales_orders(id),
                                   product_id      UUID NOT NULL REFERENCES products(id),
                                   quantity        INT NOT NULL CHECK (quantity > 0),
                                   unit_price      NUMERIC(12,2) NOT NULL,
                                   total_price     NUMERIC(14,2) GENERATED ALWAYS AS (quantity * unit_price) STORED
);

-- ───── LEDGER (Finance — auto-generated entries) ─────
CREATE TABLE ledger_entries (
                                id              SERIAL PRIMARY KEY,
                                type            VARCHAR(20) NOT NULL
                                    CHECK (type IN ('DEBIT','CREDIT')),
                                amount          NUMERIC(14,2) NOT NULL,
                                description     TEXT,
                                reference_type  VARCHAR(30),    -- 'purchase_order' | 'sales_order'
                                reference_id    UUID,
                                created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- ───── INDEXES ─────
CREATE INDEX idx_inventory_product    ON inventory(product_id);
CREATE INDEX idx_inventory_location   ON inventory(location_id);
CREATE INDEX idx_stock_mov_product    ON stock_movements(product_id);
CREATE INDEX idx_po_vendor            ON purchase_orders(vendor_id);
CREATE INDEX idx_po_status            ON purchase_orders(status);
CREATE INDEX idx_so_customer          ON sales_orders(customer_id);
CREATE INDEX idx_so_status            ON sales_orders(status);
CREATE INDEX idx_products_embedding   ON products USING hnsw (embedding vector_cosine_ops);
CREATE INDEX idx_locations_coords     ON locations USING GIST (coordinates);

-- ───── SEED ROLES ─────
INSERT INTO roles (name) VALUES
                             ('ADMIN'),
                             ('INV_MANAGER'),('INV_EMPLOYEE'),
                             ('PROC_MANAGER'),('PROC_EMPLOYEE'),
                             ('SALES_MANAGER'),('SALES_EMPLOYEE'),
                             ('FIN_MANAGER'),('FIN_EMPLOYEE'),
                             ('VENDOR'),('LOGISTICS_PARTNER'),('CUSTOMER');