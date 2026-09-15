package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rutas")
@NamedStoredProcedureQuery(
        name = "Route.reporteRendimientoRutas",
        procedureName = "sp_reporte_rendimiento_rutas",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @Column(name = "origen", nullable = false, length = 150)
    private String origin;

    @Column(name = "destino", nullable = false, length = 150)
    private String destination;

    @Column(name = "distancia_km", nullable = false)
    private Double distanceKm;

    @Column(name = "duracion_estimada_min", nullable = false)
    private Integer durationMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private RouteStatus status;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;
}
