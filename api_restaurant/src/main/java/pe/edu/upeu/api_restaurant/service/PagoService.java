package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.PagoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PagoResponse;
import pe.edu.upeu.api_restaurant.entity.Pago;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.Reserva;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import pe.edu.upeu.api_restaurant.exception.PagoInvalidoException;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.repository.PagoRepository;
import pe.edu.upeu.api_restaurant.repository.PedidoRepository;
import pe.edu.upeu.api_restaurant.repository.ReservaRepository;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PagoResponse procesar(UUID clienteId, PagoRequest request) {
        if (request.getReservaId() == null && request.getPedidoId() == null) {
            throw new PagoInvalidoException("Debe indicar una reserva o un pedido");
        }
        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        Reserva reserva = null;
        Pedido pedido = null;
        if (request.getReservaId() != null) {
            reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
            if (request.getMonto().compareTo(reserva.getMontoAdelanto()) < 0) {
                throw new PagoInvalidoException("El pago no cubre el adelanto requerido");
            }
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.getMesa().setEstado(EstadoMesa.RESERVADA);
        }
        if (request.getPedidoId() != null) {
            pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
            if (request.getTipo() != TipoPago.DELIVERY && request.getMonto().compareTo(pedido.getTotal()) < 0) {
                throw new PagoInvalidoException("El pago no cubre el total del pedido");
            }
        }
        Pago pago = pagoRepository.save(Pago.builder()
            .cliente(cliente)
            .reserva(reserva)
            .pedido(pedido)
            .monto(request.getMonto())
            .tipo(request.getTipo())
            .estado(EstadoPago.CONFIRMADO)
            .metodoPago(request.getMetodoPago())
            .referenciaPasarela("SIM-" + UUID.randomUUID())
            .fechaPago(LocalDateTime.now())
            .build());
        return toResponse(pago);
    }

    private PagoResponse toResponse(Pago pago) {
        return PagoResponse.builder()
            .id(pago.getId())
            .estado(pago.getEstado())
            .referencia(pago.getReferenciaPasarela())
            .monto(pago.getMonto())
            .tipo(pago.getTipo())
            .fechaPago(pago.getFechaPago())
            .build();
    }
}
