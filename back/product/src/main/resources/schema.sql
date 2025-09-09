CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(255),
    category VARCHAR(100),
    price DECIMAL(10,2),
    quantity INT,
    internal_reference VARCHAR(100),
    shell_id BIGINT,
    inventory_status VARCHAR(20) CHECK (inventory_status IN ('INSTOCK','LOWSTOCK','OUTOFSTOCK')),
    rating INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);