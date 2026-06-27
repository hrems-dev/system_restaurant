package pe.edu.upeu.api_restaurant.dto.request;

import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class PagoRequest {
    private UUID reservaId;
    private UUID pedidoId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;

    @NotNull
    private TipoPago tipo;

    @NotBlank
    private String metodoPago;
}
