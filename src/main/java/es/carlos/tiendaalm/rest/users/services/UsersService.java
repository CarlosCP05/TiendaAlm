package es.carlos.tiendaalm.rest.users.services;

import es.carlos.tiendaalm.rest.users.dto.UserInfoResponse;
import es.carlos.tiendaalm.rest.users.dto.UserResponse;
import es.carlos.tiendaalm.rest.users.dto.UserRequest;
import es.carlos.tiendaalm.rest.users.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {

  Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);

  UserInfoResponse findById(Long id);

  UserResponse save(UserRequest userRequest);

  UserResponse update(Long id, UserRequest userRequest);

  void deleteById(Long id);

  List<User> findAllActiveUsers();

  // Servicios usados en la parte webapp
  Optional<User> findByUsername(String username);
  void save(User user);
}
