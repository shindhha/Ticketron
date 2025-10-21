CREATE DATABASE betterStrava;
USE betterStrava;
CREATE TABLE utilisateurs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    mot_de_passe VARCHAR(80) NOT NULL,
    jwt_token BLOB
);
CREATE USER 'bs-user'@'%' IDENTIFIED BY 'bsuser';
GRANT ALL PRIVILEGES ON betterStrava.* TO 'bs-user'@'%';

INSERT INTO utilisateurs (email, nom, prenom, mot_de_passe) VALUES
    ('utilisateur@test.com', 'test', 'utilisateur', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08')