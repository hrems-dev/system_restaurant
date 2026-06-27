package pe.edu.upeu.api_restaurant.dto.request;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoPedidoRequest {
    @NotNull
    private EstadoPedido estado;
}
