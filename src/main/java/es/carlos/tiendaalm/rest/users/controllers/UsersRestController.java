package es.carlos.tiendaalm.rest.users.controllers;

import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.exceptions.AlmohadaNotFoundException;
import es.carlos.tiendaalm.rest.almohadas.services.AlmohadasService;
import es.carlos.tiendaalm.rest.users.dto.*;
import es.carlos.tiendaalm.rest.users.exceptions.UserNameOrEmailExists;
import es.carlos.tiendaalm.rest.users.exceptions.UserNotFound;
import es.carlos.tiendaalm.rest.users.models.User;
import es.carlos.tiendaalm.rest.users.services.UsersService;
import es.carlos.tiendaalm.utils.pagination.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/users")
@PreAuthorize("hasRole('USER')")
public class UsersRestController {
  private final UsersService usersService;
  private final PaginationLinksUtils paginationLinksUtils;
  private final AlmohadasService almohadasService;

  /**
   * Obtiene todos los usuarios
   *
   * @param username  username del usuario
   * @param email     email del usuario
   * @param isDeleted si está borrado o no
   * @param page      página
   * @param size      tamaño
   * @param sortBy    campo de ordenación
   * @param direction dirección de ordenación
   * @param request   petición
   * @return Respuesta con la página de usuarios
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<UserResponse>> findAll(
      @RequestParam(required = false) Optional<String> username,
      @RequestParam(required = false) Optional<String> email,
      @RequestParam(required = false) Optional<Boolean> isDeleted,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String direction,
      HttpServletRequest request
  ) {
    log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
        username, email, isDeleted, page, size, sortBy, direction);
    Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
    Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
    return ResponseEntity.ok()
        .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
        .body(PageResponse.of(pageResult, sortBy, direction));
  }

  /**
   * Obtiene un usuario por su id
   *
   * @param id del usuario, se pasa como parámetro de la URL /{id}
   * @return Usuario si existe
   * @throws UserNotFound si no existe el usuario (404)
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
    log.info("findById: id: {}", id);
    return ResponseEntity.ok(usersService.findById(id));
  }

  /**
   * Crea un nuevo usuario
   *
   * @param userRequest usuario a crear
   * @return Usuario creado
   * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
    log.info("save: userRequest: {}", userRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
  }

  /**
   * Actualiza un usuario
   *
   * @param id          id del usuario
   * @param userRequest usuario a actualizar
   * @return Usuario actualizado
   * @throws UserNotFound                        si no existe el usuario (404)
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen (400)
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
    log.info("update: id: {}, userRequest: {}", id, userRequest);
    return ResponseEntity.ok(usersService.update(id, userRequest));
  }

  /**
   * Borra un usuario
   *
   * @param id id del usuario
   * @return Respuesta vacía
   * @throws UserNotFound si no existe el usuario (404)
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    log.info("delete: id: {}", id);
    usersService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Obtiene el usuario actual
   *
   * @param user usuario autenticado
   * @return Datos del usuario
   */
  @GetMapping("/me/profile")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user) {
    log.info("Obteniendo usuario");
    return ResponseEntity.ok(usersService.findById(user.getId()));
  }

  /**
   * Actualiza el usuario actual
   *
   * @param user        usuario autenticado
   * @param userRequest usuario a actualizar
   * @return Usuario actualizado
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @PutMapping("/me/profile")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody UserRequest userRequest) {
    log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
    return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
  }

  /**
   * Borra el usuario actual
   *
   * @param user usuario autenticado
   * @return Respuesta vacía
   */
  @DeleteMapping("/me/profile")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
    log.info("deleteMe: user: {}", user);
    usersService.deleteById(user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Obtiene las almohadas del usuario actual
   *
   * @param user      usuario autenticado
   * @param page      página
   * @param size      tamaño
   * @param sortBy    campo de ordenación
   * @param direction dirección de ordenación
   * @return Respuesta con la página de almohadas
   */
  @GetMapping("/me/almohadas")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<PageResponse<AlmohadaResponseDto>> getalmohadasByUsuario(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String direction
  ) {
    log.info("Obteniendo almohadas del usuario con id: {}", user.getId());
    Sort sort = direction.equalsIgnoreCase(
        Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(PageResponse.of(
        almohadasService.findByUsuarioId(user.getId(), pageable), sortBy, direction));
  }

  /**
   * Obtiene una Almohada del usuario actual
   *
   * @param user usuario autenticado
   * @param almohadaId id de la almohada
   * @return Almohada
   * @throws AlmohadaNotFoundException si no existe la almohada
   */
  @GetMapping("/me/almohadas/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<AlmohadaResponseDto> getAlmohada(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long almohadaId
  ) {
    log.info("Obteniendo almohadas con id: {}", almohadaId);
    return ResponseEntity.ok(almohadasService.findByUsuarioId(user.getId(), almohadaId));
  }

  /**
   * Crea una Almohada para el usuario actual
   *
   * @param user usuario autenticado
   * @param almohada almohada a crear
   * @return Almohada creada
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @PostMapping("/me/almohadas")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<AlmohadaResponseDto> savealmohada(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody AlmohadaCreateDto almohada
  ) {
    log.info("Creando almohada: {}", almohada);
    return ResponseEntity.status(HttpStatus.CREATED).body(almohadasService.save(almohada, user.getId()));
  }

  /**
   * Actualiza una almohada del usuario actual
   *
   * @param user usuario autenticado
   * @param almohadaId id de la almohada
   * @param almohada a actualizar
   * @return Almohada actualizada
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   * @throws AlmohadaNotFoundException si no existe la almohada (404)
   */
  @PutMapping("/me/almohadas/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<AlmohadaResponseDto> updateAlmohada(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long almohadaId,
      @Valid @RequestBody AlmohadaUpdateDto almohada) {
    log.info("Actualizando almohadas con id: {}", almohadaId);
    return ResponseEntity.ok(almohadasService.update(almohadaId, almohada,  user.getId()));
  }

  /**
   * Borra una almohada del usuario actual
   *
   * @param user usuario autenticado
   * @param almohadaId id de la almohada
   * @return Almohada borrada
   * @throws AlmohadaNotFoundException  si no existe la almohada (404)
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @DeleteMapping("/me/almohadas/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> deleteAlmohada(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long almohadaId
  ) {
    log.info("Borrando almohadas con id: {}", almohadaId);
    almohadasService.deleteById(almohadaId,  user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Manejador de excepciones de Validación: 400 Bad Request
   *
   * @param ex excepción
   * @return Mapa de errores de validación con el campo y el mensaje
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    BindingResult result = ex.getBindingResult();
    problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
        + "'. " + "Núm. errores: " + result.getErrorCount());

    Map<String, String> errores = new HashMap<>();
    result.getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errores.put(fieldName, errorMessage);
    });

    problemDetail.setProperty("errores", errores);
    return problemDetail;
  }
}
