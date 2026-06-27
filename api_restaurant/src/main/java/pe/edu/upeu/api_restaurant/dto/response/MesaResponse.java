package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaResponse {
    private UUID id;
    private Integer numero;
    private Integer capacidad;
    private String ubicacion;
    private EstadoMesa estado;
    private String tokenQR;
    private String urlQR;
    private String imagenQRBase64;
}
