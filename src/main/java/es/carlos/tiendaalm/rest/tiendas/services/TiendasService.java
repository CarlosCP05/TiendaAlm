package es.carlos.tiendaalm.rest.tiendas.services;

import es.carlos.tiendaalm.rest.tiendas.dto.TiendaRequestDto;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TiendasService {
    Page<Tienda> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Tienda findByNombre(String nombre);

    Tienda findById(Long id);

    Tienda save(TiendaRequestDto tiendaRequestDto);

    Tienda update(Long id, TiendaRequestDto tiendaRequestDto);

    void deleteById(Long id);
}
