-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    last_login TIMESTAMP
);

-- Create cognos_servers table
CREATE TABLE cognos_servers (
    id BIGSERIAL PRIMARY KEY,
    server_name VARCHAR(100) UNIQUE NOT NULL,
    base_url VARCHAR(255) NOT NULL,
    api_key_encrypted TEXT NOT NULL,
    pronto_debtor_code VARCHAR(50) NOT NULL,
    pronto_xi_version VARCHAR(20) NOT NULL,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enrolled_by BIGINT REFERENCES users(id),
    last_poll_time TIMESTAMP,
    poll_status VARCHAR(20) CHECK (poll_status IN ('SUCCESS', 'FAILED', 'IN_PROGRESS', 'NEVER_POLLED')),
    is_active BOOLEAN DEFAULT true,
    last_error TEXT
);

-- Create server_metadata table
CREATE TABLE server_metadata (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    report_count INTEGER DEFAULT 0,
    dashboard_count INTEGER DEFAULT 0,
    data_module_count INTEGER DEFAULT 0,
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create server_content_inventory table
CREATE TABLE server_content_inventory (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('REPORT', 'PACKAGE', 'DASHBOARD', 'DATA_MODULE')),
    content_name VARCHAR(255) NOT NULL,
    content_version VARCHAR(50),
    content_path TEXT NOT NULL,
    last_updated TIMESTAMP,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for server_content_inventory
CREATE INDEX idx_content_server ON server_content_inventory(server_id);
CREATE INDEX idx_content_type ON server_content_inventory(content_type);

-- Create bulk_operations table
CREATE TABLE bulk_operations (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    operation_name VARCHAR(255),
    target_servers BIGINT[] NOT NULL,
    content_path VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED')),
    initiated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    success_count INTEGER DEFAULT 0,
    failure_count INTEGER DEFAULT 0,
    error_log TEXT
);

-- Create bulk_operation_details table
CREATE TABLE bulk_operation_details (
    id BIGSERIAL PRIMARY KEY,
    bulk_operation_id BIGINT REFERENCES bulk_operations(id) ON DELETE CASCADE,
    server_id BIGINT REFERENCES cognos_servers(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING', 'IN_PROGRESS')),
    error_message TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create change_history table
CREATE TABLE change_history (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    change_type VARCHAR(50) NOT NULL,
    change_details JSONB NOT NULL,
    changed_by BIGINT REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    initiated_from VARCHAR(20) DEFAULT 'PORTAL' CHECK (initiated_from IN ('PORTAL', 'API', 'SCHEDULED'))
);

-- Create indexes for change_history
CREATE INDEX idx_change_server ON change_history(server_id);
CREATE INDEX idx_change_date ON change_history(changed_at);

-- Create audit_log table
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(50),
    entity_id BIGINT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details JSONB,
    result VARCHAR(20) NOT NULL CHECK (result IN ('SUCCESS', 'FAILURE'))
);

-- Create indexes for audit_log
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_action ON audit_log(action);

-- Insert default admin user (password: admin123)
-- Using BCrypt hash for 'admin123'
INSERT INTO users (username, password_hash, email, role, is_active, created_at)
VALUES (
    'admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKxR9z9aO2',
    'admin@pronto.com',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP
);
