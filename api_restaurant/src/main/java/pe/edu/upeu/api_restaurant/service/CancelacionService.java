package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.CancelacionRequest;
import pe.edu.upeu.api_restaurant.dto.response.CancelacionResponse;
import pe.edu.upeu.api_restaurant.entity.Cancelacion;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.Reserva;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.repository.CancelacionRepository;
import pe.edu.upeu.api_restaurant.repository.PedidoRepository;
import pe.edu.upeu.api_restaurant.repository.ReservaRepository;
import pe.edu.upeu.api_restaurant.util.ConstantesNegocio;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelacionService {
    private final CancelacionRepository cancelacionRepository;
    private final ReservaRepository reservaRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public CancelacionResponse cancelar(CancelacionRequest request) {
        if (request.getReservaId() == null && request.getPedidoId() == null) {
            throw new ReglaNegocioException("Debe indicar reservaId o pedidoId");
        }
        Reserva reserva = null;
        Pedido pedido = null;
        BigDecimal montoOriginal;
        if (request.getReservaId() != null) {
            reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
            reserva.setEstado(EstadoReserva.CANCELADA);
            montoOriginal = reserva.getMontoAdelanto();
        } else {
            pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
            pedido.setEstado(EstadoPedido.CANCELADO);
            montoOriginal = pedido.getTotal();
        }
        BigDecimal penalidad = montoOriginal.multiply(ConstantesNegocio.PENALIDAD_CANCELACION);
        Cancelacion cancelacion = cancelacionRepository.save(Cancelacion.builder()
            .reserva(reserva)
            .pedido(pedido)
            .motivo(request.getMotivo())
            .montoOriginal(montoOriginal)
            .montoPenalidad(penalidad)
            .montoReembolso(montoOriginal.subtract(penalidad))
            .reembolsoProcesado(false)
            .build());
        return toResponse(cancelacion);
    }

    private CancelacionResponse toResponse(Cancelacion cancelacion) {
        return CancelacionResponse.builder()
            .id(cancelacion.getId())
            .motivo(cancelacion.getMotivo())
            .montoOriginal(cancelacion.getMontoOriginal())
            .montoPenalidad(cancelacion.getMontoPenalidad())
            .montoReembolso(cancelacion.getMontoReembolso())
            .reembolsoProcesado(cancelacion.isReembolsoProcesado())
            .creadaEn(cancelacion.getCreadaEn())
            .build();
    }
}
