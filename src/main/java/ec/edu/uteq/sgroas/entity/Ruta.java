package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rutas")
@NamedStoredProcedureQuery(
        name = "Ruta.reporteRendimientoRutas",
        procedureName = "sp_reporte_rendimiento_rutas",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "origen", nullable = false, length = 150)
    private String origen;

    @Column(name = "destino", nullable = false, length = 150)
    private String destino;

    @Column(name = "distancia_km", nullable = false)
    private Double distanciaKm;

    @Column(name = "duracion_estimada_min", nullable = false)
    private Integer duracionEstimadaMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoRuta estado;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}

