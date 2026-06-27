package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cancelaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @OneToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPenalidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoReembolso;

    @Column(nullable = false)
    @Builder.Default
    private boolean reembolsoProcesado = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @PrePersist
    protected void alCrear() {
        creadaEn = LocalDateTime.now();
    }
}
