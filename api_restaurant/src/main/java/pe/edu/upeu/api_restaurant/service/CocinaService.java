package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.ActualizarEstadoPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.response.OrdenCocinaResponse;
import pe.edu.upeu.api_restaurant.entity.OrdenCocina;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.repository.OrdenCocinaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CocinaService {
    private final OrdenCocinaRepository ordenCocinaRepository;

    @Transactional(readOnly = true)
    public List<OrdenCocinaResponse> listarPendientes() {
        return ordenCocinaRepository.findByEstadoPedidoIn(List.of(
            EstadoPedido.ENVIADO_COCINA,
            EstadoPedido.EN_PREPARACION))
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrdenCocinaResponse actualizar(UUID ordenId, ActualizarEstadoPedidoRequest request) {
        OrdenCocina orden = ordenCocinaRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden de cocina no encontrada"));
        orden.setEstadoPedido(request.getEstado());
        orden.getPedido().setEstado(request.getEstado());
        if (request.getEstado() == EstadoPedido.EN_PREPARACION && orden.getInicioPreparacionEn() == null) {
            orden.setInicioPreparacionEn(LocalDateTime.now());
        }
        if (request.getEstado() == EstadoPedido.LISTO && orden.getListoEn() == null) {
            orden.setListoEn(LocalDateTime.now());
        }
        return toResponse(orden);
    }

    private OrdenCocinaResponse toResponse(OrdenCocina orden) {
        return OrdenCocinaResponse.builder()
            .id(orden.getId())
            .pedidoId(orden.getPedido().getId())
            .estadoPedido(orden.getEstadoPedido())
            .prioridad(orden.getPrioridad())
            .cocineroAsignado(orden.getCocineroAsignado())
            .recibidoEn(orden.getRecibidoEn())
            .inicioPreparacionEn(orden.getInicioPreparacionEn())
            .listoEn(orden.getListoEn())
            .build();
    }
}
