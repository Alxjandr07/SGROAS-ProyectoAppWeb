package ec.edu.uteq.sgroas.abd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Driver del esquema ABD (tabla "conductor"). Convive con Driver (tabla "conductores").
 */
@Entity
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbdDriver {

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
