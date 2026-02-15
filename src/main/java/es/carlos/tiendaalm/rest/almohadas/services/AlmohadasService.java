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

    Page<AlmohadaResponseDto> findByUsuarioId(Long id, Pageable pageable);

    AlmohadaResponseDto findByUsuarioId(Long usuarioId, Long almohadaId);

    AlmohadaResponseDto save(AlmohadaCreateDto almohadaCreateDto);

    AlmohadaResponseDto save(AlmohadaCreateDto almohadaCreateDto, Long usuarioId);

    AlmohadaResponseDto update(Long id, AlmohadaUpdateDto almohadaUpdateDto);

    AlmohadaResponseDto update(Long id, AlmohadaUpdateDto almohadaUpdateDto, Long usuarioId);

    void deleteById(Long id);

    void deleteById(Long id, Long usuarioId);
}
