package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "codigos_qr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoQR {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String urlAcceso;

    @Column(columnDefinition = "text")
    private String imagenBase64;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
