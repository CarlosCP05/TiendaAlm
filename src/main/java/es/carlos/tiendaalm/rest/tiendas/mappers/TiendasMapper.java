package es.carlos.tiendaalm.rest.tiendas.mappers;

import es.carlos.tiendaalm.rest.tiendas.dto.TiendaRequestDto;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TiendasMapper {
    public Tienda toTienda(TiendaRequestDto dto) {
        return Tienda.builder().
            id(null).
            nombre(dto.getNombre()).
            build();
    }

    public Tienda toTienda(TiendaRequestDto dto, Tienda tienda) {
        return Tienda.builder().
                id(tienda.getId()).
                nombre(dto.getNombre() != null ? dto.getNombre() : tienda.getNombre()).
                fechaCreacion(tienda.getFechaCreacion()).
                isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false).
                build();
    }
}
