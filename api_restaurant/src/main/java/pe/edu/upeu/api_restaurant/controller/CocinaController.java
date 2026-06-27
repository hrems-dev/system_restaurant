package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.ActualizarEstadoPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.response.OrdenCocinaResponse;
import pe.edu.upeu.api_restaurant.service.CocinaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cocina")
@RequiredArgsConstructor
public class CocinaController {
    private final CocinaService cocinaService;

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenCocinaResponse>> listarPendientes() {
        return ResponseEntity.ok(cocinaService.listarPendientes());
    }

    @PatchMapping("/ordenes/{ordenId}")
    public ResponseEntity<OrdenCocinaResponse> actualizar(@PathVariable UUID ordenId,
                                                          @Valid @RequestBody ActualizarEstadoPedidoRequest request) {
        return ResponseEntity.ok(cocinaService.actualizar(ordenId, request));
    }
}
