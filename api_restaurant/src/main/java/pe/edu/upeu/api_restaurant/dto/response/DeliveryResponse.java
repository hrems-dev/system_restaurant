package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResponse {
    private UUID id;
    private UUID pedidoId;
    private String repartidor;
    private String direccionEntrega;
    private EstadoDelivery estado;
    private BigDecimal costoDelivery;
    private LocalDateTime creadoEn;
    private LocalDateTime salidaEn;
    private LocalDateTime entregadoEn;
}
