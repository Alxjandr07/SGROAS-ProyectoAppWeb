-- V2__seed.sql
-- Datos semilla para desarrollo.
-- Cada usuario tiene su propia contrasena (bcrypt strength 10), generadas con
-- org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder:
--   admin@sgroas.com       -> admin123
--   coordinador@sgroas.com -> coord123
--   seguridad@sgroas.com   -> segur123

INSERT INTO usuarios (nombre, email, password_hash, rol, activo)
VALUES
  ('Admin SGROAS', 'admin@sgroas.com', '$2a$10$907rvTzvJycyHHY5ZcLZaOHj18xmdnLpek/9aqS1snYTJsKVuqtZ.', 'ROLE_ADMIN', true),
  ('Coordinador Principal', 'coordinador@sgroas.com', '$2a$10$8rSHyBtb4GCqIoLLmGsVT.GwhYNeJHu0gCXnsjiDfFD5VyzvdInUe', 'ROLE_COORDINADOR', true),
  ('Seguridad General', 'seguridad@sgroas.com', '$2a$10$/69fl48bF4qoy73F7/cSceBcou8bg4nuTSXOVAqFf8pkqib7aSKfS', 'ROLE_SEGURIDAD', true)
ON CONFLICT DO NOTHING;
