-- System roles initialization
INSERT INTO roles (name, description)
VALUES ('ADMIN', 'System administrator with all permissions') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('OWNER', 'Restaurant owner') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('EMPLOYEE', 'Restaurant employee') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('CLIENT', 'Food court client') ON CONFLICT (name) DO NOTHING;

-- Default admin user (password: admin123)
INSERT INTO users (first_name, last_name, identity_document, phone, birth_date, email, password, role_id)
SELECT 'Admin', 'System', '1234567890', '+573000000000', '1990-01-01', 'admin@plazoleta.com',
       '$2a$10$N9qo8uLOickgx2ZMRZoMyu.qY.Cz3G3Yn1vYGvI5p1qLqrIpAvfBK', r.id
FROM roles r WHERE r.name = 'ADMIN'
ON CONFLICT (email) DO NOTHING;
