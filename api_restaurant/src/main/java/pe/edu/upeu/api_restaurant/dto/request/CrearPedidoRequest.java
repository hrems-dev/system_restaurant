package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CrearPedidoRequest {
    private UUID mesaId;
    private boolean esDelivery;
    private String direccionDelivery;
    private String notas;

    @Valid
    @NotEmpty
    private List<DetallePedidoRequest> detalles;
}
