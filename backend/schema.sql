-- SpendWise - Personal Expense & Budget Manager Database Schema
-- Remote Database: MySQL / PostgreSQL Compatible

CREATE DATABASE IF NOT EXISTS spendwise_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spendwise_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT DEFAULT NULL,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50) DEFAULT 'ic_category',
    type ENUM('INCOME', 'EXPENSE') DEFAULT 'EXPENSE',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    client_uuid VARCHAR(64) DEFAULT NULL UNIQUE,
    category_id INT DEFAULT NULL,
    type ENUM('income', 'expense') NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description TEXT DEFAULT NULL,
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Budgets Table
CREATE TABLE IF NOT EXISTS budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    budget_month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY user_category_month (user_id, category_name, budget_month),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample Default Categories Data
INSERT INTO categories (user_id, name, type) VALUES
(NULL, 'Groceries', 'EXPENSE'),
(NULL, 'Transport', 'EXPENSE'),
(NULL, 'Utilities', 'EXPENSE'),
(NULL, 'Entertainment', 'EXPENSE'),
(NULL, 'Salary', 'INCOME'),
(NULL, 'Freelance', 'INCOME')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Sample Test User (Password: Password123)
INSERT INTO users (id, full_name, email, password_hash) VALUES
(1, 'Naomi Akilimali', 'naomi@example.com', '$2y$10$e8T/k64m0/X0Hn58I3gL.e.s.e8e8e8e8e8e8e8e8e8e8e8e8e8e')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
