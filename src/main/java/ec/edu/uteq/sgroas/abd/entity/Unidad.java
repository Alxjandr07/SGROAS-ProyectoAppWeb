package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Unidad del esquema ABD (tabla "unidad"): vehiculo con placa y numero de disco unicos.
 * Convive con Vehiculo (tabla "vehiculos").
 */
@Entity
@Table(name = "unidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Integer idUnidad;

    @Column(name = "placa", nullable = false, unique = true, length = 15)
    private String placa;

    @Column(name = "numero_disco", nullable = false, unique = true, length = 10)
    private String numeroDisco;

    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "anio_fabricacion")
    private Integer anioFabricacion;

    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private String estado = "Activo";
}
