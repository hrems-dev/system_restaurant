package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.response.FacturaResponse;
import pe.edu.upeu.api_restaurant.service.FacturaService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class FacturaController {
    private final FacturaService facturaService;

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<FacturaResponse> emitir(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(facturaService.emitir(pedidoId));
    }
}
