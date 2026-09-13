package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    /**
     * Consulta los vehiculos activos de forma paginada.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los vehiculos activos.
     */
    Page<Vehiculo> findByActivoTrue(Pageable pageable);

    /**
     * Consulta si existe un vehiculo con la placa dada.
     * @param placa placa a verificar.
     * @return verdadero si existe un vehiculo con esa placa.
     */
    boolean existsByPlaca(String placa);

    /**
     * Consulta los vehiculos en mantenimiento mediante procedimiento almacenado.
     * @return filas del procedimiento con los vehiculos en mantenimiento.
     */
    @Procedure(name = "Vehiculo.vehiculosEnMantenimiento")
    List<Object[]> vehiculosEnMantenimiento();
}
