package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.DeliveryRequest;
import pe.edu.upeu.api_restaurant.dto.response.DeliveryResponse;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import pe.edu.upeu.api_restaurant.service.DeliveryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> crear(@Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> listar() {
        return ResponseEntity.ok(deliveryService.listar());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DeliveryResponse> cambiarEstado(@PathVariable UUID id, @RequestParam EstadoDelivery estado) {
        return ResponseEntity.ok(deliveryService.cambiarEstado(id, estado));
    }
}
