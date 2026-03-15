CREATE DATABASE IF NOT EXISTS amdox_taskdb;
USE amdox_taskdb;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(20) DEFAULT 'TODO',
    due_date DATE,
    assigned_to BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message TEXT NOT NULL,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_task_status ON tasks(status);
CREATE INDEX idx_task_assigned ON tasks(assigned_to);
CREATE INDEX idx_comment_task ON comments(task_id);
CREATE INDEX idx_comment_user ON comments(user_id);

INSERT INTO users (name, email, password, role, active) VALUES
('Admin User', 'admin@amdox.com', '$2a$10$YFq4Y4KH3CxJZd.Z8jz2iufQsKEJvKV8D6c1HMcJ2yKGWaYMcKYi.', 'ADMIN', TRUE),
('John Editor', 'john@amdox.com', '$2a$10$YFq4Y4KH3CxJZd.Z8jz2iufQsKEJvKV8D6c1HMcJ2yKGWaYMcKYi.', 'EDITOR', TRUE),
('Jane Viewer', 'jane@amdox.com', '$2a$10$YFq4Y4KH3CxJZd.Z8jz2iufQsKEJvKV8D6c1HMcJ2yKGWaYMcKYi.', 'VIEWER', TRUE);
