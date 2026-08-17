package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Page<Vehiculo> findByActivoTrue(Pageable pageable);

    boolean existsByPlaca(String placa);

    @Procedure(procedureName = "sp_vehiculos_en_mantenimiento", outputParameterName = "cur")
    List<Object[]> vehiculosEnMantenimiento();
}
