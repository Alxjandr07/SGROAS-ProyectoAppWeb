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
@Table(name = "vehiculos")
@NamedStoredProcedureQuery(
        name = "Vehicle.vehiculosEnMantenimiento",
        procedureName = "sp_vehiculos_en_mantenimiento",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", nullable = false, unique = true, length = 20)
    private String plate;

    @Column(name = "marca", nullable = false, length = 50)
    private String brand;

    @Column(name = "modelo", nullable = false, length = 50)
    private String model;

    @Column(name = "anio", nullable = false)
    private Integer year;

    @Column(name = "capacidad_pasajeros", nullable = false)
    private Integer capacity;

    @Column(name = "numero_motor", length = 50)
    private String engineNumber;

    @Column(name = "numero_chasis", length = 50)
    private String chassisNumber;

    @Column(name = "color", length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 25)
    private VehicleStatus status;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;
}
