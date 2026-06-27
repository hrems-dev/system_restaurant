# Flujo de uso de la API Restaurante

Este documento describe el flujo principal para usar el proyecto `api_restaurant`.

Base URL local:

```text
http://localhost:8080/api
```

## 1. Levantar servicios

Levantar PostgreSQL:

```powershell
docker compose up -d
```

Iniciar la API:

```powershell
.\gradlew.bat bootRun
```

Verificar menu publico:

```http
GET /api/menu
```

Si responde `200`, la API esta activa.

## 2. Autenticacion

### Registrar usuario

```http
POST /api/auth/registrar
Content-Type: application/json
```

```json
{
  "email": "cliente@test.com",
  "contrasena": "cliente123",
  "nombre": "Cliente Test",
  "telefono": "999999999",
  "rol": "CLIENTE"
}
```

Respuesta esperada:

```json
{
  "token": "...",
  "tipo": "Bearer",
  "usuarioId": "...",
  "email": "cliente@test.com",
  "nombre": "Cliente Test",
  "rol": "CLIENTE"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "cliente@test.com",
  "contrasena": "cliente123"
}
```

Para rutas protegidas usar:

```http
Authorization: Bearer <token>
```

## 3. Flujo de mesa y QR

### Crear mesa

Ruta protegida:

```http
POST /api/mesas
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "numero": 1,
  "capacidad": 4,
  "ubicacion": "Interior"
}
```

La respuesta incluye:

- `tokenQR`
- `urlQR`
- `imagenQRBase64`

### Consultar mesa por QR

Ruta publica:

```http
GET /api/mesas/qr/{tokenQR}
```

Este endpoint simula el acceso del cliente al escanear el QR.

## 4. Flujo de menu

### Ver menu completo

Ruta publica:

```http
GET /api/menu
```

### Ver menu por categoria

Ruta publica:

```http
GET /api/menu/categoria/{categoriaId}
```

## 5. Flujo de reserva con adelanto

### Crear reserva

Ruta protegida:

```http
POST /api/reservas
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "mesaId": "uuid-de-mesa",
  "fechaHoraInicio": "2026-07-01T19:00:00",
  "fechaHoraFin": "2026-07-01T21:00:00",
  "numeroPersonas": 4,
  "montoAdelanto": 30.00,
  "notasEspeciales": "Mesa cerca a ventana"
}
```

Estado inicial esperado:

```text
PENDIENTE_PAGO
```

Reglas aplicadas:

- La fecha final debe ser posterior a la inicial.
- La mesa debe existir.
- La cantidad de personas no puede superar la capacidad de la mesa.
- La reserva requiere adelanto mayor que cero.
- No debe existir otra reserva solapada para la misma mesa.

### Procesar pago de adelanto

Ruta protegida:

```http
POST /api/pagos/procesar
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "reservaId": "uuid-de-reserva",
  "monto": 30.00,
  "tipo": "ADELANTO_RESERVA",
  "metodoPago": "tarjeta"
}
```

Resultado esperado:

- Pago queda `CONFIRMADO`.
- Reserva pasa a `CONFIRMADA`.
- Mesa pasa a `RESERVADA`.

## 6. Flujo de pedido

### Crear pedido en mesa

Ruta protegida:

```http
POST /api/pedidos
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "mesaId": "uuid-de-mesa",
  "esDelivery": false,
  "notas": "Sin cebolla",
  "detalles": [
    {
      "productoId": "uuid-de-producto",
      "cantidad": 2,
      "observaciones": "Poco picante"
    }
  ]
}
```

Resultado esperado:

- Pedido pasa a `ENVIADO_COCINA`.
- Mesa pasa a `OCUPADA`.
- Se genera una orden de cocina.
- El total se calcula con precio del producto por cantidad.

### Crear pedido delivery

Ruta protegida:

```http
POST /api/pedidos
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "esDelivery": true,
  "direccionDelivery": "Av. Principal 123",
  "notas": "Llamar al llegar",
  "detalles": [
    {
      "productoId": "uuid-de-producto",
      "cantidad": 1
    }
  ]
}
```

Para delivery no se requiere `mesaId`.

## 7. Flujo de cocina

### Listar ordenes pendientes

Ruta protegida:

```http
GET /api/cocina/ordenes
Authorization: Bearer <token>
```

Lista ordenes en:

- `ENVIADO_COCINA`
- `EN_PREPARACION`

### Cambiar estado de una orden

Ruta protegida:

```http
PATCH /api/cocina/ordenes/{ordenId}
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "estado": "EN_PREPARACION"
}
```

Estados habituales:

- `EN_PREPARACION`
- `LISTO`
- `ENTREGADO`
- `CANCELADO`

## 8. Flujo de delivery

### Crear delivery

Ruta protegida:

```http
POST /api/delivery
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "pedidoId": "uuid-de-pedido",
  "repartidorId": "uuid-de-repartidor",
  "direccionEntrega": "Av. Principal 123",
  "costoDelivery": 8.00
}
```

Si se asigna repartidor, el estado inicial sera:

```text
ASIGNADO
```

Si no se asigna repartidor:

```text
PENDIENTE
```

### Listar deliveries

```http
GET /api/delivery
Authorization: Bearer <token>
```

### Cambiar estado de delivery

```http
PATCH /api/delivery/{id}/estado?estado=EN_CAMINO
Authorization: Bearer <token>
```

Estados disponibles:

- `PENDIENTE`
- `ASIGNADO`
- `EN_CAMINO`
- `ENTREGADO`
- `FALLIDO`

## 9. Flujo de pago final

Ruta protegida:

```http
POST /api/pagos/procesar
Authorization: Bearer <token>
Content-Type: application/json
```

Pago de consumo en mesa:

```json
{
  "pedidoId": "uuid-de-pedido",
  "monto": 96.00,
  "tipo": "CONSUMO_FINAL",
  "metodoPago": "tarjeta"
}
```

Pago delivery:

```json
{
  "pedidoId": "uuid-de-pedido",
  "monto": 104.00,
  "tipo": "DELIVERY",
  "metodoPago": "yape"
}
```

Resultado esperado:

```text
Pago CONFIRMADO
```

## 10. Flujo de facturacion

Ruta protegida:

```http
POST /api/facturas/pedido/{pedidoId}
Authorization: Bearer <token>
```

Resultado esperado:

- Numero de factura generado.
- Subtotal calculado.
- IGV calculado.
- Total del pedido.

## 11. Flujo de cancelacion

Ruta protegida:

```http
POST /api/cancelaciones
Authorization: Bearer <token>
Content-Type: application/json
```

Cancelar reserva:

```json
{
  "reservaId": "uuid-de-reserva",
  "motivo": "Cliente cancelo"
}
```

Cancelar pedido:

```json
{
  "pedidoId": "uuid-de-pedido",
  "motivo": "Producto no disponible"
}
```

Resultado esperado:

- Reserva pasa a `CANCELADA`, o pedido pasa a `CANCELADO`.
- Se calcula penalidad.
- Se calcula reembolso.

## 12. Flujo de reportes

### Reporte de ventas

```http
GET /api/reportes/ventas
Authorization: Bearer <token>
```

### Reporte de ocupacion de mesas

```http
GET /api/reportes/ocupacion
Authorization: Bearer <token>
```

### Productos mas vendidos

```http
GET /api/reportes/productos-mas-vendidos
Authorization: Bearer <token>
```

## 13. Orden recomendado para probar todo

1. `docker compose up -d`
2. `.\gradlew.bat bootRun`
3. `GET /api/menu`
4. `POST /api/auth/registrar`
5. Copiar token JWT.
6. `POST /api/mesas`
7. `GET /api/mesas/qr/{tokenQR}`
8. `POST /api/reservas`
9. `POST /api/pagos/procesar` con `ADELANTO_RESERVA`
10. `POST /api/pedidos`
11. `GET /api/cocina/ordenes`
12. `PATCH /api/cocina/ordenes/{ordenId}`
13. `POST /api/pagos/procesar` con `CONSUMO_FINAL` o `DELIVERY`
14. `POST /api/facturas/pedido/{pedidoId}`
15. `GET /api/reportes/ventas`

## 14. Estados principales

Mesa:

- `DISPONIBLE`
- `RESERVADA`
- `OCUPADA`
- `PENDIENTE_PAGO`
- `EN_LIMPIEZA`
- `BLOQUEADA`

Reserva:

- `PENDIENTE_PAGO`
- `CONFIRMADA`
- `ACTIVA`
- `COMPLETADA`
- `CANCELADA`
- `NO_ASISTIO`

Pedido:

- `PENDIENTE`
- `ENVIADO_COCINA`
- `EN_PREPARACION`
- `LISTO`
- `ENTREGADO`
- `CANCELADO`

Pago:

- `PENDIENTE`
- `PROCESANDO`
- `CONFIRMADO`
- `RECHAZADO`
- `REEMBOLSADO`

Delivery:

- `PENDIENTE`
- `ASIGNADO`
- `EN_CAMINO`
- `ENTREGADO`
- `FALLIDO`

