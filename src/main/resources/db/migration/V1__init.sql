CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identity_document VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(13) NOT NULL,
    birth_date DATE,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE credentials (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_credential_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE employee_restaurants (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    restaurant_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_restaurant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index for common lookups
CREATE INDEX idx_users_identity_document ON users(identity_document);
CREATE INDEX idx_credentials_email ON credentials(email);
CREATE INDEX idx_employee_restaurants_restaurant_id ON employee_restaurants(restaurant_id);

-- Initial Roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'System administrator with all permissions'),
('OWNER', 'Restaurant owner'),
('EMPLOYEE', 'Restaurant employee'),
('CLIENT', 'Food court client');
