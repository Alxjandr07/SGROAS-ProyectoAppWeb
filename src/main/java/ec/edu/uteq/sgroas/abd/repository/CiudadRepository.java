package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    /**
     * Consulta las ciudades de una provincia ordenadas por identificador ascendente.
     * @param idProvincia identificador de la provincia.
     * @return lista de ciudades de la provincia.
     */
    List<Ciudad> findByProvinciaIdProvinciaOrderByIdCiudadAsc(Integer idProvincia);
}
