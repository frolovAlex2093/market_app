CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    img_path    VARCHAR(255) NOT NULL,
    price       BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id   BIGINT    NOT NULL,
    created   TIMESTAMP NOT NULL,
    total_sum BIGINT    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT  NOT NULL,
    item_id  BIGINT  NOT NULL,
    quantity INTEGER NOT NULL,
    price    BIGINT  NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE IF NOT EXISTS cart_items
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT  NOT NULL,
    item_id  BIGINT  NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (item_id) REFERENCES items (id)
);