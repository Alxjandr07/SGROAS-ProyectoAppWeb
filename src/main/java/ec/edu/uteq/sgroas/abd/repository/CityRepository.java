package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {

    /**
     * Consulta las ciudades de una provincia ordenadas por identificador ascendente.
     * @param idProvincia identificador de la provincia.
     * @return lista de ciudades de la provincia.
     */
    List<City> findByProvinciaIdProvinciaOrderByIdCiudadAsc(Integer idProvincia);
}
