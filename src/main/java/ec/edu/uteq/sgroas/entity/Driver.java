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
@Table(name = "conductores")
@NamedStoredProcedureQuery(
        name = "Driver.licenciasPorVencer",
        procedureName = "fn_licencias_por_vencer",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_dias_umbral", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "cur", type = Class.class)
        })
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String firstNames;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String lastNames;

    @Column(name = "cedula", nullable = false, unique = true, length = 10)
    private String nationalId;

    @Column(name = "numero_licencia", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    @Column(name = "tipo_licencia", nullable = false, length = 10)
    private String licenseType;

    @Column(name = "fecha_vencimiento_licencia", nullable = false)
    private LocalDate licenseExpiry;

    @Column(name = "telefono", length = 20)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private DriverStatus status;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;
}
