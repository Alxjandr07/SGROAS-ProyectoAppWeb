package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Alerta derivada de un incidente con nivel de riesgo (esquema ABD).
 */
@Entity
@Table(name = "alerta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @Column(name = "nivel_riesgo", nullable = false, length = 20)
    private String nivelRiesgo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    /** EAGER: las alertas se serializan a JSON fuera de la transaccion. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_incidente", nullable = false)
    private IncidenteAbd incidente;
}
