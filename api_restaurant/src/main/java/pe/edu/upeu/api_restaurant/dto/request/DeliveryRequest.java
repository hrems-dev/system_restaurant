package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryRequest {
    @NotNull
    private UUID pedidoId;

    private UUID repartidorId;

    @NotBlank
    private String direccionEntrega;

    @DecimalMin("0.00")
    private BigDecimal costoDelivery;
}
