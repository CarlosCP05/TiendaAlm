package es.carlos.tiendaalm.rest.almohadas.services;

import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AlmohadasService {
    Page<AlmohadaResponseDto> findAll(Optional<String> firmeza, Optional<Tacto> tacto, Optional<Boolean> isDeleted, Pageable pageable);

    AlmohadaResponseDto findById(Long id);

    AlmohadaResponseDto save(AlmohadaCreateDto almohadaCreateDto);

    //cambiar a AlmohadaUpateDto
    AlmohadaResponseDto update(Long id, AlmohadaUpdateDto almohadaUpdateDto);

    void deleteById(Long id);
}
