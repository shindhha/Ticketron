CREATE DATABASE ticketron;
USE ticketron;
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant VARCHAR(255),
    date DATE,
    total_amount DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'EUR',
    category_code VARCHAR(100),
    description TEXT,
    image_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES categories(code)
);


CREATE USER 'tt-user'@'%' IDENTIFIED BY 'ttuser';
GRANT ALL PRIVILEGES ON ticketron.* TO 'tt-user'@'%';

