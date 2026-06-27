package pe.edu.upeu.api_restaurant.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacturaResponse {
    private UUID id;
    private String numeroFactura;
    private UUID pedidoId;
    private String nombreCliente;
    private String rucCliente;
    private String razonSocial;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDateTime emitidaEn;
}
