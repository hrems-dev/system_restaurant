package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.DeliveryRequest;
import pe.edu.upeu.api_restaurant.dto.response.DeliveryResponse;
import pe.edu.upeu.api_restaurant.entity.Delivery;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.repository.DeliveryRepository;
import pe.edu.upeu.api_restaurant.repository.PedidoRepository;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public DeliveryResponse crear(DeliveryRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
        Usuario repartidor = null;
        if (request.getRepartidorId() != null) {
            repartidor = usuarioRepository.findById(request.getRepartidorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor no encontrado"));
        }
        Delivery delivery = deliveryRepository.save(Delivery.builder()
            .pedido(pedido)
            .repartidor(repartidor)
            .direccionEntrega(request.getDireccionEntrega())
            .costoDelivery(request.getCostoDelivery() != null ? request.getCostoDelivery() : BigDecimal.ZERO)
            .estado(repartidor != null ? EstadoDelivery.ASIGNADO : EstadoDelivery.PENDIENTE)
            .build());
        return toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse cambiarEstado(UUID id, EstadoDelivery estado) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Delivery no encontrado"));
        delivery.setEstado(estado);
        if (estado == EstadoDelivery.EN_CAMINO && delivery.getSalidaEn() == null) {
            delivery.setSalidaEn(LocalDateTime.now());
        }
        if (estado == EstadoDelivery.ENTREGADO && delivery.getEntregadoEn() == null) {
            delivery.setEntregadoEn(LocalDateTime.now());
        }
        return toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> listar() {
        return deliveryRepository.findAll().stream().map(this::toResponse).toList();
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
            .id(delivery.getId())
            .pedidoId(delivery.getPedido().getId())
            .repartidor(delivery.getRepartidor() != null ? delivery.getRepartidor().getNombre() : null)
            .direccionEntrega(delivery.getDireccionEntrega())
            .estado(delivery.getEstado())
            .costoDelivery(delivery.getCostoDelivery())
            .creadoEn(delivery.getCreadoEn())
            .salidaEn(delivery.getSalidaEn())
            .entregadoEn(delivery.getEntregadoEn())
            .build();
    }
}
