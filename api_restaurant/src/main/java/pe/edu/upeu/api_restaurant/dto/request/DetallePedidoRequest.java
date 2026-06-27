package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class DetallePedidoRequest {
    @NotNull
    private UUID productoId;

    @NotNull
    @Min(1)
    @Max(50)
    private Integer cantidad;

    private String observaciones;
}
