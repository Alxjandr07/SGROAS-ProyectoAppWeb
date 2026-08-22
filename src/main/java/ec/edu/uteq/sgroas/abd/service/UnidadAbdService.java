package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.Unidad;
import ec.edu.uteq.sgroas.abd.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UnidadAbdService {

    private final UnidadRepository unidadRepository;

    @Transactional(readOnly = true)
    public Page<AbdDtos.UnidadResponse> listar(String estado, Pageable pageable) {
        Page<Unidad> page = (estado == null || estado.isBlank())
                ? unidadRepository.findAll(pageable)
                : unidadRepository.findByEstadoIgnoreCase(estado, pageable);
        return page.map(this::aResponse);
    }

    @Transactional(readOnly = true)
    public AbdDtos.UnidadResponse buscarPorId(Long idUnidad) {
        return unidadRepository.findById(idUnidad).map(this::aResponse)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + idUnidad));
    }

    public AbdDtos.UnidadResponse crear(AbdDtos.UnidadRequest request) {
        validarUnicidad(request.placa(), request.numeroDisco(), null);
        Unidad unidad = Unidad.builder()
                .placa(request.placa())
                .numeroDisco(request.numeroDisco())
                .modelo(request.modelo())
                .capacidad(request.capacidad())
                .anioFabricacion(request.anioFabricacion())
                .estado(request.estado() == null ? "Activo" : request.estado())
                .build();
        return aResponse(unidadRepository.save(unidad));
    }

    public AbdDtos.UnidadResponse actualizar(Long idUnidad, AbdDtos.UnidadRequest request) {
        Unidad unidad = unidadRepository.findById(idUnidad)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + idUnidad));
        validarUnicidad(request.placa(), request.numeroDisco(), idUnidad);
        unidad.setPlaca(request.placa());
        unidad.setNumeroDisco(request.numeroDisco());
        unidad.setModelo(request.modelo());
        unidad.setCapacidad(request.capacidad());
        unidad.setAnioFabricacion(request.anioFabricacion());
        if (request.estado() != null) {
            unidad.setEstado(request.estado());
        }
        return aResponse(unidadRepository.save(unidad));
    }

    public void eliminar(Long idUnidad) {
        if (!unidadRepository.existsById(idUnidad)) {
            throw new IllegalArgumentException("Unidad no encontrada: " + idUnidad);
        }
        unidadRepository.deleteById(idUnidad);
    }

    private void validarUnicidad(String placa, String numeroDisco, Long idExcluir) {
        if (idExcluir == null) {
            if (unidadRepository.existsByPlacaIgnoreCase(placa)) {
                throw new IllegalArgumentException("Ya existe una unidad con la placa " + placa);
            }
            if (unidadRepository.existsByNumeroDiscoIgnoreCase(numeroDisco)) {
                throw new IllegalArgumentException("Ya existe una unidad con el disco " + numeroDisco);
            }
            return;
        }
        Unidad actual = unidadRepository.findById(idExcluir)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + idExcluir));
        boolean cambioPlaca = !actual.getPlaca().equalsIgnoreCase(placa);
        boolean cambioDisco = !actual.getNumeroDisco().equalsIgnoreCase(numeroDisco);
        if (cambioPlaca && unidadRepository.existsByPlacaIgnoreCase(placa)) {
            throw new IllegalArgumentException("Ya existe una unidad con la placa " + placa);
        }
        if (cambioDisco && unidadRepository.existsByNumeroDiscoIgnoreCase(numeroDisco)) {
            throw new IllegalArgumentException("Ya existe una unidad con el disco " + numeroDisco);
        }
    }

    private AbdDtos.UnidadResponse aResponse(Unidad u) {
        return new AbdDtos.UnidadResponse(u.getIdUnidad(), u.getPlaca(), u.getNumeroDisco(),
                u.getModelo(), u.getCapacidad(), u.getAnioFabricacion(), u.getEstado());
    }
}
