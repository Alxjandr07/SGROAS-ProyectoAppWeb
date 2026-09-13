package ec.edu.uteq.sgroas.abd.entity;

import ec.edu.uteq.sgroas.abd.entity.AbdRoute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Schedule del esquema ABD: asigna ruta + unidad + conductor a una fecha/hora.
 * Equivalente enriquecido de asignacion_rutas del modulo web.
 */
@Entity
@Table(name = "programacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programacion")
    private Integer idProgramacion;

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
    private AbdRoute ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unit unidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor", nullable = false)
    private AbdDriver conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private AbdUser usuarioRegistro;
}
