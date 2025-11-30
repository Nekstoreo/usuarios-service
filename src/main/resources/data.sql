-- System roles initialization
INSERT INTO roles (name, description)
VALUES ('ADMIN', 'System administrator with all permissions') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('OWNER', 'Restaurant owner') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('EMPLOYEE', 'Restaurant employee') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description)
VALUES ('CLIENT', 'Food court client') ON CONFLICT (name) DO NOTHING;
