package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.CancelacionRequest;
import pe.edu.upeu.api_restaurant.dto.response.CancelacionResponse;
import pe.edu.upeu.api_restaurant.service.CancelacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cancelaciones")
@RequiredArgsConstructor
public class CancelacionController {
    private final CancelacionService cancelacionService;

    @PostMapping
    public ResponseEntity<CancelacionResponse> cancelar(@Valid @RequestBody CancelacionRequest request) {
        return ResponseEntity.ok(cancelacionService.cancelar(request));
    }
}
