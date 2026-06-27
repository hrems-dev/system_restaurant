package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.CrearReservaRequest;
import pe.edu.upeu.api_restaurant.dto.response.ReservaResponse;
import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.entity.Reserva;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.mapper.ReservaMapper;
import pe.edu.upeu.api_restaurant.repository.MesaRepository;
import pe.edu.upeu.api_restaurant.repository.ReservaRepository;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;

    @Transactional
    public ReservaResponse crear(UUID clienteId, CrearReservaRequest request) {
        if (!request.getFechaHoraFin().isAfter(request.getFechaHoraInicio())) {
            throw new ReglaNegocioException("La fecha final debe ser posterior a la fecha inicial");
        }
        if (request.getMontoAdelanto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("Toda reserva requiere adelanto");
        }
        Mesa mesa = mesaRepository.findById(request.getMesaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada"));
        if (request.getNumeroPersonas() > mesa.getCapacidad()) {
            throw new ReglaNegocioException("La mesa no tiene capacidad suficiente");
        }
        boolean ocupada = reservaRepository.existeSolapamiento(
            mesa.getId(),
            request.getFechaHoraInicio(),
            request.getFechaHoraFin(),
            List.of(EstadoReserva.PENDIENTE_PAGO, EstadoReserva.CONFIRMADA, EstadoReserva.ACTIVA));
        if (ocupada) {
            throw new ReglaNegocioException("La mesa ya tiene una reserva en ese horario");
        }
        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        Reserva reserva = Reserva.builder()
            .mesa(mesa)
            .cliente(cliente)
            .fechaHoraInicio(request.getFechaHoraInicio())
            .fechaHoraFin(request.getFechaHoraFin())
            .numeroPersonas(request.getNumeroPersonas())
            .montoAdelanto(request.getMontoAdelanto())
            .notasEspeciales(request.getNotasEspeciales())
            .estado(EstadoReserva.PENDIENTE_PAGO)
            .build();
        mesa.setEstado(EstadoMesa.PENDIENTE_PAGO);
        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        return reservaRepository.findAll().stream().map(reservaMapper::toResponse).toList();
    }
}
