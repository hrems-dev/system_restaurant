package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservaResponse {
    private UUID id;
    private UUID mesaId;
    private Integer numeroMesa;
    private UUID clienteId;
    private String nombreCliente;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer numeroPersonas;
    private BigDecimal montoAdelanto;
    private EstadoReserva estado;
    private String notasEspeciales;
}
