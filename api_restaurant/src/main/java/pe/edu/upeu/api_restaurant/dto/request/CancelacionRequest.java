package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class CancelacionRequest {
    private UUID reservaId;
    private UUID pedidoId;

    @NotBlank
    private String motivo;
}
