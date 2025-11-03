CREATE DATABASE ticketron;
USE ticketron;
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary TEXT,
    category_code VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES categories(code)
);


CREATE USER 'tt-user'@'%' IDENTIFIED BY 'ttuser';
GRANT ALL PRIVILEGES ON ticketron.* TO 'tt-user'@'%';

