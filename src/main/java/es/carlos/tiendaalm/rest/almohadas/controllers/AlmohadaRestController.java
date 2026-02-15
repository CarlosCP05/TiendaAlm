package es.carlos.tiendaalm.rest.almohadas.controllers;

import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.models.Tacto;
import es.carlos.tiendaalm.rest.almohadas.repositories.AlmohadasRepository;
import es.carlos.tiendaalm.rest.almohadas.services.AlmohadasService;
import es.carlos.tiendaalm.utils.pagination.PageResponse;
import es.carlos.tiendaalm.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${api.version:v1}/almohadas")
public class AlmohadaRestController {

    private final AlmohadasRepository almohadasRepository;
    private final AlmohadasService almohadasService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping()
    public ResponseEntity<PageResponse<AlmohadaResponseDto>> getAll(
        @RequestParam(required = false) Optional<String> firmeza,
        @RequestParam(required = false) Optional<Tacto> tacto,
        @RequestParam(required = false) Optional<Boolean> isDeleted,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        HttpServletRequest request){
        log.info("Buscando almohadas por tacto={} y firmeza={}", tacto, firmeza);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<AlmohadaResponseDto> pageResult = almohadasService.findAll(firmeza, tacto, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlmohadaResponseDto> getAlmohadaById(@PathVariable Long id) {
        log.info("Buscando almohada por id {}", id);
        return ResponseEntity.ok(almohadasService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<AlmohadaResponseDto> createAlmohada(@Valid @RequestBody AlmohadaCreateDto almohadaCreateDto) {
        log.info("Creando almohada {}", almohadaCreateDto);
        var saved = almohadasService.save(almohadaCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlmohadaResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlmohadaUpdateDto almohadaUpdateDto) {
        log.info("Actualizando almohada con id: {}, con contenido {}", id, almohadaUpdateDto);
        return ResponseEntity.ok(almohadasService.update(id, almohadaUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlmohadaResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody AlmohadaUpdateDto almohadaUpdateDto) {
        log.info("Actualizando parcialmente almohada con id: {} con contenido {}", id, almohadaUpdateDto);
        return ResponseEntity.ok(almohadasService.update(id, almohadaUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Eliminando almohada con id: {}", id);
        almohadasService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName() + "'.\nNúm. errores: " + result.getErrorCount() );

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}