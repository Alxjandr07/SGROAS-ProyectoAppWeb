package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
        name = "RouteAssignment.asignacionesActivasPorConductor",
        procedureName = "sp_asignaciones_activas_por_conductor",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_conductor_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class RouteAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Route route;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private AssignmentStatus status;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;
}
