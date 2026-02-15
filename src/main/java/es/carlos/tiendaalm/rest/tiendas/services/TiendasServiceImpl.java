package es.carlos.tiendaalm.rest.tiendas.services;

import es.carlos.tiendaalm.rest.tiendas.dto.TiendaRequestDto;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import es.carlos.tiendaalm.rest.tiendas.exceptions.TiendaConflictException;
import es.carlos.tiendaalm.rest.tiendas.exceptions.TiendaNotFoundException;
import es.carlos.tiendaalm.rest.tiendas.mappers.TiendasMapper;
import es.carlos.tiendaalm.rest.tiendas.repositories.TiendasRepository;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"tiendas"})
public class TiendasServiceImpl implements TiendasService {
    private final TiendasRepository tiendasRepository;
    private final TiendasMapper tiendasMapper;

    @Override
    public Page<Tienda> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando tiendas por nombre: {}, isDeleted: {}",  nombre, isDeleted);

        Specification<Tienda> specNombreTienda = (root, query, criteriaBuilder) ->
            nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%")).
            orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tienda> specIsDeleted = (root, query, criteriaBuilder) ->
            isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d)).
            orElseGet(()  -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tienda> criterio = Specification.allOf(specNombreTienda, specIsDeleted);
        return tiendasRepository.findAll(criterio, pageable);
    }

    @Override
    public Tienda findByNombre(String nombre) {
        log.info("Buscando tiendas por nombre: {}", nombre);
        return tiendasRepository.findByNombreEqualsIgnoreCase(nombre).
            orElseThrow(() -> new TiendaNotFoundException(nombre));
    }

    @Override
    @CachePut(key = "#id")
    public Tienda findById(Long id) {
        log.info("Buscando tiendas por id: {}", id);
        return  tiendasRepository.findById(id).
            orElseThrow(() -> new TiendaNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Tienda save(TiendaRequestDto tiendaRequestDto) {
        log.info("Guardando tienda : {}", tiendaRequestDto);
        tiendasRepository.findByNombreEqualsIgnoreCase(tiendaRequestDto.getNombre()).ifPresent(tie -> {
            throw new TiendaConflictException("Ya existe una tienda con el nombre " + tiendaRequestDto.getNombre());
        });
        return tiendasRepository.save(tiendasMapper.toTienda(tiendaRequestDto));
    }

    @Override
    @CachePut(key = "#result.id")
    public Tienda update(Long id, TiendaRequestDto tiendaRequestDto) {
        log.info("Actualizando tienda: {}", tiendaRequestDto);
        Tienda tiendaactual = findById(id);
        tiendasRepository.findByNombreEqualsIgnoreCase(tiendaRequestDto.getNombre()).ifPresent(tie -> {
            throw new TiendaNotFoundException("Ya existe una tienda con el nombre " + tiendaRequestDto.getNombre());
        });
        return tiendasRepository.save(tiendasMapper.toTienda(tiendaRequestDto, tiendaactual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando tienda por id: {}", id);
        Tienda tienda = findById(id);
        //Usar existsAlmohada o StockById?
        if (tiendasRepository.existsById(id)) {
            //Tendre que cambiarlo para que funcione con stock? revisar
            String mensaje = "No se puede eliminar el tienda con el id: " + id + " porque tiene un stock asociado";
            log.warn(mensaje);
            throw new TiendaNotFoundException(mensaje);
        } else {
            tiendasRepository.deleteById(id);
        }
    }
}
