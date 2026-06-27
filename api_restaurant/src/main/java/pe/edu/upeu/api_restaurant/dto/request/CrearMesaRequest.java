package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearMesaRequest {
    @NotNull
    @Min(1)
    private Integer numero;

    @NotNull
    @Min(1)
    private Integer capacidad;

    private String ubicacion;
}
