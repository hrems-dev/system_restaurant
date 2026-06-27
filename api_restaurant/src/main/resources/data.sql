INSERT INTO categorias (id, nombre, descripcion, orden, activa)
VALUES
    (uuid_generate_v4(), 'Entradas', 'Platos para comenzar', 1, true),
    (uuid_generate_v4(), 'Platos de fondo', 'Platos principales', 2, true),
    (uuid_generate_v4(), 'Postres', 'Dulces y postres', 3, true),
    (uuid_generate_v4(), 'Bebidas', 'Bebidas frias y calientes', 4, true),
    (uuid_generate_v4(), 'Delivery especial', 'Solo para pedidos delivery', 5, true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios (id, nombre, email, contrasena, rol, activo)
VALUES (
    uuid_generate_v4(),
    'Administrador',
    'admin@restaurante.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
    'ADMIN',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Ceviche clasico', 'Ceviche de pescado con leche de tigre', 35.00, true, 10, c.id
FROM categorias c
WHERE c.nombre = 'Entradas' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Ceviche clasico');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Tequenos', '6 tequenos con queso crema', 18.00, true, 8, c.id
FROM categorias c
WHERE c.nombre = 'Entradas' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Tequenos');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Lomo saltado', 'Lomo saltado con papas y arroz', 48.00, true, 15, c.id
FROM categorias c
WHERE c.nombre = 'Platos de fondo' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Lomo saltado');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Aji de gallina', 'Aji de gallina con arroz y papas', 42.00, true, 12, c.id
FROM categorias c
WHERE c.nombre = 'Platos de fondo' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Aji de gallina');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Arroz con leche', 'Postre tradicional peruano', 12.00, true, 0, c.id
FROM categorias c
WHERE c.nombre = 'Postres' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Arroz con leche');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Agua mineral', 'Agua mineral 600ml', 5.00, true, 0, c.id
FROM categorias c
WHERE c.nombre = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Agua mineral');

INSERT INTO productos_menu (id, nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
SELECT uuid_generate_v4(), 'Inca Kola', 'Gaseosa 500ml', 6.00, true, 0, c.id
FROM categorias c
WHERE c.nombre = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM productos_menu p WHERE p.nombre = 'Inca Kola');
