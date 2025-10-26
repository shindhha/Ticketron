CREATE DATABASE ticketron;
USE ticketron;
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code CHAR(4) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant VARCHAR(255),
    date DATE,
    total_amount DECIMAL(10,2),
    vat_amount DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'EUR',
    category_code CHAR(4),
    description TEXT,
    payment_method VARCHAR(50),
    image_path VARCHAR(500),
    confidence FLOAT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES categories(code)
);

CREATE TABLE expense_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    employee_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'DRAFT'
);

ALTER TABLE expenses
ADD COLUMN report_id BIGINT,
ADD FOREIGN KEY (report_id) REFERENCES expense_reports(id);
CREATE USER 'tt-user'@'%' IDENTIFIED BY 'ttuser';
GRANT ALL PRIVILEGES ON ticketron.* TO 'tt-user'@'%';

