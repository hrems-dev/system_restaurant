package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CrearReservaRequest {
    @NotNull
    private UUID mesaId;

    @NotNull
    @Future
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Future
    private LocalDateTime fechaHoraFin;

    @NotNull
    @Min(1)
    private Integer numeroPersonas;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal montoAdelanto;

    private String notasEspeciales;
}
