package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagoResponse {
    private UUID id;
    private EstadoPago estado;
    private String referencia;
    private BigDecimal monto;
    private TipoPago tipo;
    private LocalDateTime fechaPago;
}
