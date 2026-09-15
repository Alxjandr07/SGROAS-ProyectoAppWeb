package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Page<Vehicle> findByActiveTrue(Pageable pageable);

    boolean existsByPlate(String plate);

    @Procedure(name = "Vehicle.vehiculosEnMantenimiento")
    List<Object[]> vehiclesInMaintenance();
}
