package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.response.MenuResponse;
import pe.edu.upeu.api_restaurant.entity.ProductoMenu;
import pe.edu.upeu.api_restaurant.repository.ProductoMenuRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final ProductoMenuRepository productoMenuRepository;

    @GetMapping
    public ResponseEntity<List<MenuResponse>> listarMenu() {
        return ResponseEntity.ok(productoMenuRepository.findByDisponibleTrue().stream().map(this::toResponse).toList());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<MenuResponse>> listarPorCategoria(@PathVariable UUID categoriaId) {
        return ResponseEntity.ok(productoMenuRepository.findByCategoriaId(categoriaId).stream()
            .filter(ProductoMenu::isDisponible)
            .map(this::toResponse)
            .toList());
    }

    private MenuResponse toResponse(ProductoMenu producto) {
        return MenuResponse.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .descripcion(producto.getDescripcion())
            .precio(producto.getPrecio())
            .imagenUrl(producto.getImagenUrl())
            .tiempoPreparacionMinutos(producto.getTiempoPreparacionMinutos())
            .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
            .build();
    }
}
