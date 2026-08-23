package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Incidente del esquema ABD (tabla "incidente"): ligado directamente a una unidad.
 * Convive con Incidente (tabla "incidentes").
 */
@Entity
@Table(name = "incidente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenteAbd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidente")
    private Integer idIncidente;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "nivel_sugerido", nullable = false, length = 20)
    private String nivelSugerido;

    @Column(name = "fecha_incidente", nullable = false)
    @Builder.Default
    private LocalDateTime fechaIncidente = LocalDateTime.now();

    @Column(name = "evidencia", length = 255)
    private String evidencia;

    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "Reportado";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unidad unidad;
}
