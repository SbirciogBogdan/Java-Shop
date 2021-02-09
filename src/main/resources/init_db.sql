-- main tables (from specifications)
DROP TABLE IF EXISTS suppliers CASCADE;
CREATE TABLE suppliers
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(255),
    description TEXT
);

DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(255),
    department  VARCHAR(255),
    description TEXT
);

DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products
(
    id              SERIAL PRIMARY KEY NOT NULL,
    supplier_id     INTEGER REFERENCES suppliers (id) ON DELETE CASCADE,
    category_id     INTEGER REFERENCES categories (id) ON DELETE SET NULL,
    name            VARCHAR(255),
    description     TEXT,
    image_file_name VARCHAR(255)
);

DROP TABLE IF EXISTS prices CASCADE;
CREATE TABLE prices
(
    id          SERIAL PRIMARY KEY NOT NULL,
    product_id  INTEGER REFERENCES products (id) ON DELETE CASCADE,
    sum         FLOAT,
    currency    VARCHAR(10),
    date        DATE
);

DROP TABLE IF EXISTS addresses CASCADE;
CREATE TABLE addresses
(
    id              SERIAL PRIMARY KEY NOT NULL,
    country         VARCHAR(100),
    city            VARCHAR(100),
    zipcode         VARCHAR(10),
    home_address    VARCHAR(100)
);

DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS UserStatus;
CREATE TYPE UserStatus AS ENUM('SIGNED', 'UNSIGNED');
CREATE TABLE users
(
    id              SERIAL PRIMARY KEY NOT NULL,
    name            VARCHAR(100),
    email           VARCHAR(100),
    password        VARCHAR(100),
    phone_number    VARCHAR(30),
    billing_id      INTEGER REFERENCES addresses (id) ON DELETE SET NULL,
    shipping_id     INTEGER REFERENCES addresses (id) ON DELETE SET NULL,
    user_status     UserStatus
);

DROP TABLE IF EXISTS orders CASCADE;
DROP TYPE IF EXISTS OrderStatus;
CREATE TYPE OrderStatus AS ENUM('CHECKED', 'PAID', 'CONFIRMED', 'SHIPPED');
CREATE TABLE orders
(
    id              SERIAL PRIMARY KEY NOT NULL,
    name            VARCHAR(50),
    user_id         INTEGER REFERENCES users (id) ON DELETE CASCADE,
    is_my_cart      BOOLEAN NOT NULL,
    date            DATE,
    order_status    OrderStatus
);

DROP TABLE IF EXISTS items CASCADE;
CREATE TABLE items
(
    id          SERIAL PRIMARY KEY NOT NULL,
    price_id    INTEGER REFERENCES prices (id) ON DELETE SET NULL,
    order_id    INTEGER REFERENCES orders (id) ON DELETE CASCADE,
    quantity    INTEGER
);

DROP TABLE IF EXISTS events CASCADE;
CREATE TABLE events
(
    id              SERIAL PRIMARY KEY NOT NULL,
    date            DATE,
    description     VARCHAR(100),
    order_id        INTEGER REFERENCES orders(id) ON DELETE CASCADE
);

