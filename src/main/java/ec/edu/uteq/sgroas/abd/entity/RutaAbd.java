package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Ruta del esquema ABD (tabla "ruta"): conecta dos terminales con precio de pasaje.
 * Convive con Ruta (tabla "rutas").
 */
@Entity
@Table(name = "ruta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaAbd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terminal_origen", nullable = false)
    private Terminal terminalOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terminal_destino", nullable = false)
    private Terminal terminalDestino;

    @Column(name = "precio_pasaje", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPasaje;
}
