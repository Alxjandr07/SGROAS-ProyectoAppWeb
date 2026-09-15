package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
        name = "Incident.incidentesPorGravedad",
        procedureName = "sp_incidentes_por_gravedad",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_tipo", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
@NamedStoredProcedureQuery(
        name = "Incident.obtenerIncidentesPorRango",
        procedureName = "sp_obtener_incidentes_por_rango",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_desde", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_hasta", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
@NamedStoredProcedureQuery(
        name = "Incident.estadisticasGenerales",
        procedureName = "fn_estadisticas_generales",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private RouteAssignment assignment;

    @Column(name = "reportado_por", nullable = false, length = 100)
    private String reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 25)
    private IncidentType type;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "fecha_incidente", nullable = false)
    private LocalDateTime incidentDate;

    @Column(name = "ubicacion", length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "gravedad", nullable = false, length = 10)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private IncidentStatus status;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;
}
