package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.CrearMesaRequest;
import pe.edu.upeu.api_restaurant.dto.response.MesaResponse;
import pe.edu.upeu.api_restaurant.entity.CodigoQR;
import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.mapper.MesaMapper;
import pe.edu.upeu.api_restaurant.repository.CodigoQRRepository;
import pe.edu.upeu.api_restaurant.repository.MesaRepository;
import pe.edu.upeu.api_restaurant.util.GeneradorQR;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MesaService {
    private final MesaRepository mesaRepository;
    private final CodigoQRRepository codigoQRRepository;
    private final GeneradorQR generadorQR;
    private final MesaMapper mesaMapper;

    @Transactional
    public MesaResponse crear(CrearMesaRequest request) {
        if (mesaRepository.existsByNumero(request.getNumero())) {
            throw new ReglaNegocioException("Ya existe una mesa con ese numero");
        }
        Mesa mesa = mesaRepository.save(Mesa.builder()
            .numero(request.getNumero())
            .capacidad(request.getCapacidad())
            .ubicacion(request.getUbicacion())
            .build());

        String token = UUID.randomUUID().toString();
        String url = "http://localhost:8080/api/mesas/qr/" + token;
        CodigoQR qr = codigoQRRepository.save(CodigoQR.builder()
            .mesa(mesa)
            .token(token)
            .urlAcceso(url)
            .imagenBase64(generadorQR.generarBase64(url))
            .activo(true)
            .build());
        mesa.setCodigoQR(qr);
        return mesaMapper.toResponse(mesa);
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listar() {
        return mesaRepository.findAll().stream().map(mesaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MesaResponse buscarPorQr(String token) {
        return codigoQRRepository.findByTokenAndActivoTrue(token)
            .map(CodigoQR::getMesa)
            .map(mesaMapper::toResponse)
            .orElseThrow(() -> new RecursoNoEncontradoException("Codigo QR no encontrado"));
    }
}
