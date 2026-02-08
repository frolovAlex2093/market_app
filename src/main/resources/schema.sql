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
    created   TIMESTAMP NOT NULL,
    total_sum BIGINT    NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT  NOT NULL,
    item_id  BIGINT  NOT NULL,
    quantity INTEGER NOT NULL,
    price    BIGINT  NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    item_id    BIGINT       NOT NULL,
    quantity   INTEGER      NOT NULL
    );