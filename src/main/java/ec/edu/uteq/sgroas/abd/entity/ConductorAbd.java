package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Conductor del esquema ABD (tabla "conductor"). Convive con Conductor (tabla "conductores").
 */
@Entity
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConductorAbd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conductor")
    private Integer idConductor;

    @Column(name = "cedula", nullable = false, unique = true, length = 10)
    private String cedula;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "licencia", nullable = false, length = 30)
    private String licencia;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "telefono", nullable = false, length = 25)
    private String telefono;
}
