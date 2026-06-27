package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.LoginRequest;
import pe.edu.upeu.api_restaurant.dto.request.RegistroUsuarioRequest;
import pe.edu.upeu.api_restaurant.dto.response.AuthResponse;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.mapper.UsuarioMapper;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import pe.edu.upeu.api_restaurant.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacionService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioMapper usuarioMapper;

    public AuthResponse registrar(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("El email ya esta registrado");
        }
        Usuario usuario = Usuario.builder()
            .email(request.getEmail())
            .contrasena(passwordEncoder.encode(request.getContrasena()))
            .nombre(request.getNombre())
            .telefono(request.getTelefono())
            .rol(request.getRol())
            .activo(true)
            .build();
        Usuario guardado = usuarioRepository.save(usuario);
        String token = jwtUtil.generarToken(guardado.getEmail(), guardado.getRol().name(), guardado.getId());
        return usuarioMapper.toAuthResponse(guardado, token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena()));
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ReglaNegocioException("Credenciales invalidas"));
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name(), usuario.getId());
        return usuarioMapper.toAuthResponse(usuario, token);
    }
}
