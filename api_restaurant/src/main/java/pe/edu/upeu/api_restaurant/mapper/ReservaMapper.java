package pe.edu.upeu.api_restaurant.mapper;

import pe.edu.upeu.api_restaurant.dto.response.ReservaResponse;
import pe.edu.upeu.api_restaurant.entity.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {
    public ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
            .id(reserva.getId())
            .mesaId(reserva.getMesa().getId())
            .numeroMesa(reserva.getMesa().getNumero())
            .clienteId(reserva.getCliente().getId())
            .nombreCliente(reserva.getCliente().getNombre())
            .fechaHoraInicio(reserva.getFechaHoraInicio())
            .fechaHoraFin(reserva.getFechaHoraFin())
            .numeroPersonas(reserva.getNumeroPersonas())
            .montoAdelanto(reserva.getMontoAdelanto())
            .estado(reserva.getEstado())
            .notasEspeciales(reserva.getNotasEspeciales())
            .build();
    }
}
