package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdenCocinaResponse {
    private UUID id;
    private UUID pedidoId;
    private EstadoPedido estadoPedido;
    private Integer prioridad;
    private String cocineroAsignado;
    private LocalDateTime recibidoEn;
    private LocalDateTime inicioPreparacionEn;
    private LocalDateTime listoEn;
}
