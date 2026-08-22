package ec.edu.uteq.sgroas.abd.entity;

import ec.edu.uteq.sgroas.abd.entity.RutaAbd;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Programacion del esquema ABD: asigna ruta + unidad + conductor a una fecha/hora.
 * Equivalente enriquecido de asignacion_rutas del modulo web.
 */
@Entity
@Table(name = "programacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Programacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programacion")
    private Long idProgramacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "hora_estimada_llegada", nullable = false)
    private LocalTime horaEstimadaLlegada;

    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private String estado = "Programado";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private RutaAbd ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unidad unidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor", nullable = false)
    private ConductorAbd conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private UsuarioAbd usuarioRegistro;
}
