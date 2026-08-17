package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "incidentes")
@NamedStoredProcedureQuery(
        name = "Incidente.incidentesPorGravedad",
        procedureName = "sp_incidentes_por_gravedad",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_tipo", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
@NamedStoredProcedureQuery(
        name = "Incidente.obtenerIncidentesPorRango",
        procedureName = "sp_obtener_incidentes_por_rango",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_desde", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_hasta", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
@NamedStoredProcedureQuery(
        name = "Incidente.estadisticasGenerales",
        procedureName = "fn_estadisticas_generales",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private AsignacionRuta asignacion;

    @Column(name = "reportado_por", nullable = false, length = 100)
    private String reportadoPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 25)
    private TipoIncidente tipo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_incidente", nullable = false)
    private LocalDateTime fechaIncidente;

    @Column(name = "ubicacion", length = 255)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "gravedad", nullable = false, length = 10)
    private GravedadIncidente gravedad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoIncidente estado;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}

