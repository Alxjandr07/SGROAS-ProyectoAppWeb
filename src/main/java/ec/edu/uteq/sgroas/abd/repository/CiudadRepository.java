package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {

    List<Ciudad> findByProvinciaIdProvinciaOrderByIdCiudadAsc(Long idProvincia);
}
