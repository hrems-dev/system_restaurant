# Flujos de procesos de negocio

Este documento explica los flujos de negocio principales del restaurante. No se enfoca en codigo, sino en como se mueve cada proceso desde que inicia hasta que termina.

## 1. Flujo de venta en mesa

Objetivo: atender a un cliente que consume dentro del restaurante.

```text
Cliente llega
  -> Se asigna o usa una mesa
  -> Mesa pasa a OCUPADA
  -> Cliente revisa menu
  -> Se crea pedido
  -> Pedido pasa a ENVIADO_COCINA
  -> Cocina prepara pedido
  -> Pedido pasa a LISTO
  -> Pedido se entrega al cliente
  -> Pedido pasa a ENTREGADO
  -> Cliente paga consumo final
  -> Pago queda CONFIRMADO
  -> Se emite factura
  -> Mesa puede volver a DISPONIBLE o EN_LIMPIEZA
```

Actores:

- Cliente.
- Mesero.
- Cocina.
- Caja o sistema de pagos.

Estados importantes:

- Mesa: `DISPONIBLE`, `OCUPADA`, `PENDIENTE_PAGO`, `EN_LIMPIEZA`.
- Pedido: `ENVIADO_COCINA`, `EN_PREPARACION`, `LISTO`, `ENTREGADO`.
- Pago: `CONFIRMADO`.

Resultado final:

- Pedido entregado.
- Pago confirmado.
- Factura emitida.

## 2. Flujo de servicio de cocina

Objetivo: preparar los pedidos enviados desde mesa o delivery.

```text
Pedido creado
  -> Sistema genera orden de cocina
  -> Orden entra como ENVIADO_COCINA
  -> Cocinero toma la orden
  -> Orden pasa a EN_PREPARACION
  -> Cocina prepara los productos
  -> Orden pasa a LISTO
  -> Mesero o repartidor recoge el pedido
  -> Pedido pasa a ENTREGADO cuando llega al cliente
```

Actores:

- Cocinero.
- Mesero.
- Repartidor, si es delivery.

Estados importantes:

- `ENVIADO_COCINA`: cocina aun no inicia.
- `EN_PREPARACION`: cocina esta preparando.
- `LISTO`: pedido terminado y listo para entregar.
- `ENTREGADO`: pedido ya fue recibido por el cliente.

Resultado final:

- Pedido preparado y listo para entregar.

## 3. Flujo de reserva con adelanto

Objetivo: asegurar una mesa para una fecha y hora, cobrando adelanto.

```text
Cliente solicita reserva
  -> Sistema valida mesa, capacidad y horario
  -> Se crea reserva
  -> Reserva queda PENDIENTE_PAGO
  -> Mesa queda PENDIENTE_PAGO
  -> Cliente paga adelanto
  -> Pago queda CONFIRMADO
  -> Reserva pasa a CONFIRMADA
  -> Mesa pasa a RESERVADA
  -> Cliente llega al restaurante
  -> Reserva pasa a ACTIVA
  -> Mesa pasa a OCUPADA
  -> Cliente consume
  -> Cliente paga consumo final
  -> Reserva pasa a COMPLETADA
```

Actores:

- Cliente.
- Recepcion o administrador.
- Sistema de pagos.

Reglas de negocio:

- La reserva requiere adelanto.
- No puede existir cruce de horarios para la misma mesa.
- La cantidad de personas no puede superar la capacidad.
- La fecha final debe ser posterior a la inicial.

Resultado final:

- Reserva confirmada si se paga el adelanto.
- Reserva completada si el cliente asiste y consume.

## 4. Flujo de pedido por QR

Objetivo: permitir que el cliente consulte la mesa y menu desde el QR.

```text
Mesa tiene QR generado
  -> Cliente escanea QR
  -> Sistema identifica mesa por token QR
  -> Cliente consulta menu publico
  -> Cliente elige productos
  -> Se crea pedido asociado a mesa
  -> Pedido pasa a ENVIADO_COCINA
  -> Cocina prepara
  -> Pedido se entrega
  -> Cliente paga
```

Actores:

- Cliente.
- Sistema.
- Cocina.
- Mesero.

Resultado final:

- Pedido asociado a una mesa real.
- Menor intervencion manual del mesero al inicio.

## 5. Flujo de venta delivery

Objetivo: vender productos para entrega a domicilio.

```text
Cliente crea pedido delivery
  -> Pedido no requiere mesa
  -> Pedido incluye direccion de entrega
  -> Pedido pasa a ENVIADO_COCINA
  -> Cocina prepara pedido
  -> Pedido pasa a LISTO
  -> Se crea registro delivery
  -> Se asigna repartidor
  -> Delivery pasa a ASIGNADO
  -> Repartidor recoge pedido
  -> Delivery pasa a EN_CAMINO
  -> Repartidor entrega pedido
  -> Delivery pasa a ENTREGADO
  -> Pago queda CONFIRMADO
  -> Se puede emitir factura
```

Actores:

- Cliente.
- Cocina.
- Repartidor.
- Sistema de pagos.

Estados importantes:

- Pedido: `ENVIADO_COCINA`, `EN_PREPARACION`, `LISTO`, `ENTREGADO`.
- Delivery: `PENDIENTE`, `ASIGNADO`, `EN_CAMINO`, `ENTREGADO`, `FALLIDO`.
- Pago: `CONFIRMADO`.

Resultado final:

- Pedido entregado al domicilio.
- Pago confirmado.

## 6. Flujo de pago

Objetivo: confirmar economicamente una reserva, consumo en mesa o delivery.

```text
Cliente inicia pago
  -> Sistema recibe monto, tipo y metodo
  -> Sistema valida referencia relacionada
  -> Si es reserva, valida adelanto
  -> Si es pedido en mesa, valida total del pedido
  -> Sistema simula pasarela
  -> Pago queda CONFIRMADO
  -> Se actualiza el proceso relacionado
```

Tipos de pago:

- `ADELANTO_RESERVA`: confirma una reserva.
- `CONSUMO_FINAL`: paga consumo de mesa.
- `DELIVERY`: paga pedido delivery.

Resultado final:

- Pago confirmado.
- Reserva, pedido o delivery puede avanzar al siguiente estado.

## 7. Flujo de facturacion

Objetivo: emitir comprobante por un pedido pagado o listo para facturar.

```text
Pedido existe
  -> Sistema calcula subtotal
  -> Sistema calcula IGV
  -> Sistema toma total del pedido
  -> Sistema genera numero de factura
  -> Factura queda emitida
```

Actores:

- Caja.
- Administrador.
- Sistema.

Datos principales:

- Numero de factura.
- Cliente.
- Pedido.
- Subtotal.
- IGV.
- Total.

Resultado final:

- Factura emitida y asociada al pedido.

## 8. Flujo de cancelacion

Objetivo: registrar la cancelacion de una reserva o pedido y calcular penalidad/reembolso.

```text
Cliente o personal solicita cancelacion
  -> Sistema identifica reserva o pedido
  -> Sistema cambia estado a CANCELADA o CANCELADO
  -> Sistema calcula monto original
  -> Sistema calcula penalidad
  -> Sistema calcula reembolso
  -> Cancelacion queda registrada
```

Casos:

- Cancelacion de reserva.
- Cancelacion de pedido.

Reglas:

- Debe existir una reserva o pedido.
- Debe registrarse motivo.
- Se calcula penalidad sobre el monto original.

Resultado final:

- Proceso cancelado.
- Penalidad y reembolso calculados.

## 9. Flujo de reportes

Objetivo: consultar informacion resumida para administracion.

```text
Administrador solicita reporte
  -> Sistema consulta datos
  -> Sistema agrupa totales
  -> Sistema responde resumen
```

Reportes disponibles:

- Ventas.
- Ocupacion de mesas.
- Productos mas vendidos.

Uso en negocio:

- Revisar ingresos.
- Medir uso de mesas.
- Identificar productos con mayor salida.

## 10. Flujo general resumido

```text
Reserva o llegada directa
  -> Mesa o delivery
  -> Pedido
  -> Cocina
  -> Entrega
  -> Pago
  -> Factura
  -> Reporte
```

Si hay problema:

```text
Reserva o pedido
  -> Cancelacion
  -> Penalidad/Reembolso
  -> Reporte
```

