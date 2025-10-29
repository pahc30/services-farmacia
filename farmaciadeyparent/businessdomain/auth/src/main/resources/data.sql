-- Usuario de prueba para Selenium: test1/test1
-- Password BCrypt hash para "test1": $2a$10$e/O4o5U/0byEgubpshl/EOA2TTzhIQxB4JIzoVu.0cimkr4YPmiii
INSERT INTO Usuario (identificacion, nombres, apellidos, telefono, email, direccion, rol, username, password, eliminado)
SELECT '00000001', 'Test', 'User', '', 'test1@example.com', '', 'USUARIO', 'test1', '$2a$10$e/O4o5U/0byEgubpshl/EOA2TTzhIQxB4JIzoVu.0cimkr4YPmiii', 0
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE username = 'test1');
