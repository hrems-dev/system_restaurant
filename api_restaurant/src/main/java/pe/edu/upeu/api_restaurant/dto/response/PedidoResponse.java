package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidoResponse {
    private UUID id;
    private EstadoPedido estado;
    private BigDecimal total;
    private boolean esDelivery;
    private LocalDateTime creadoEn;
}
