package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "asignacion_rutas")
@NamedStoredProcedureQuery(
        name = "AsignacionRuta.asignacionesActivasPorConductor",
        procedureName = "sp_asignaciones_activas_por_conductor",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_conductor_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class AsignacionRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoAsignacion estado;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}

