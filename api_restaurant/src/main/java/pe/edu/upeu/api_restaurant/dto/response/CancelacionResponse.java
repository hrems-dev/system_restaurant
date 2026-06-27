package pe.edu.upeu.api_restaurant.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelacionResponse {
    private UUID id;
    private String motivo;
    private BigDecimal montoOriginal;
    private BigDecimal montoPenalidad;
    private BigDecimal montoReembolso;
    private boolean reembolsoProcesado;
    private LocalDateTime creadaEn;
}
