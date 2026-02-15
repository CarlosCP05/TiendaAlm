package es.carlos.tiendaalm.rest.users.services;

import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.almohadas.repositories.AlmohadasRepository;
import es.carlos.tiendaalm.rest.users.dto.UserInfoResponse;
import es.carlos.tiendaalm.rest.users.dto.UserRequest;
import es.carlos.tiendaalm.rest.users.dto.UserResponse;
import es.carlos.tiendaalm.rest.users.exceptions.UserNameOrEmailExists;
import es.carlos.tiendaalm.rest.users.exceptions.UserNotFound;
import es.carlos.tiendaalm.rest.users.mappers.UsersMapper;
import es.carlos.tiendaalm.rest.users.models.User;
import es.carlos.tiendaalm.rest.users.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

  private final UsersRepository usersRepository;
  private final UsersMapper usersMapper;
  private final AlmohadasRepository almohadasRepository;

  @Override
  public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
    log.info("Buscando todos los usuarios con username: {} y borrados: {}", username, isDeleted);
    Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
        username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
        email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
        isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    Specification<User> criterio = Specification.allOf(
        specUsernameUser,
        specEmailUser,
        specIsDeleted
    );

    return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
  }


  //Cambiarlo para buscar desde tienda o stock?
  @Override
  @Cacheable(key = "#id")
  public UserInfoResponse findById(Long id) {
    log.info("Buscando usuario por id: {}", id);
    var user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
    var almohadas = almohadasRepository.findByUsuarioId(id).stream().map(Almohada::getId).toList();
    return usersMapper.toUserInfoResponse(user,  almohadas);
  }

  @Override
  @CachePut(key = "#result.id")
  public UserResponse save(UserRequest userRequest) {
    log.info("Guardando usuario: {}", userRequest);
    usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
        .ifPresent(u -> {
          throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
        });
    return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
  }

  @Override
  @CachePut(key = "#result.id")
  public UserResponse update(Long id, UserRequest userRequest) {
    log.info("Actualizando usuario: {}", userRequest);
    usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
    usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
        .ifPresent(u -> {
          if (!u.getId().equals(id)) {
            System.out.println("usuario encontrado: " + u.getId() + " Mi id: " + id);
            throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
          }
        });
    return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
  }

  @Override
  @Transactional
  @CacheEvict(key = "#id")
  public void deleteById(Long id) {
    log.info("Borrando usuario por id: {}", id);
    User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
    if (almohadasRepository.existsById(id)) {
      log.info("Borrado lógico de usuario por id: {}", id);
      usersRepository.updateIsDeletedToTrueById(id);
    } else {
      log.info("Borrado físico de usuario por id: {}", id);
      usersRepository.delete(user);
    }
  }

  public List<User> findAllActiveUsers() {
    log.info("Buscando todos los usuarios activos");
    return usersRepository.findAllByIsDeletedFalse();
  }

  public Optional<User> findByUsername(String username) {
      return usersRepository.findByUsername(username);
  }

  public void save(User user) {
    usersRepository.save(user);
  }
}
