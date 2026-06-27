package pe.edu.upeu.api_restaurant.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer tiempoPreparacionMinutos;
    private String categoriaNombre;
}
