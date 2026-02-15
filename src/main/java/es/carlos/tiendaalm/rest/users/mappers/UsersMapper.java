package es.carlos.tiendaalm.rest.users.mappers;

import es.carlos.tiendaalm.rest.users.dto.UserInfoResponse;
import es.carlos.tiendaalm.rest.users.dto.UserRequest;
import es.carlos.tiendaalm.rest.users.dto.UserResponse;
import es.carlos.tiendaalm.rest.users.models.User;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class UsersMapper {
  public User toUser(UserRequest request) {
    return User.builder()
        .nombre(request.getNombre())
        .apellidos(request.getApellidos())
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .roles(request.getRoles())
        .isDeleted(request.getIsDeleted())
        .build();
  }

  public User toUser(UserRequest request, Long id) {
    return User.builder()
        .id(id)
        .nombre(request.getNombre())
        .apellidos(request.getApellidos())
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .roles(request.getRoles())
        .isDeleted(request.getIsDeleted())
        .build();
  }

  public UserResponse toUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .nombre(user.getNombre())
        .apellidos(user.getApellidos())
        .username(user.getUsername())
        .email(user.getEmail())
        .roles(user.getRoles())
        .isDeleted(user.getIsDeleted())
        .build();
  }

  public UserInfoResponse toUserInfoResponse(User user, List<Long> almohadas) {
    return UserInfoResponse.builder()
        .id(user.getId())
        .nombre(user.getNombre())
        .apellidos(user.getApellidos())
        .username(user.getUsername())
        .email(user.getEmail())
        .roles(user.getRoles())
        .isDeleted(user.getIsDeleted())
        .almohadas(almohadas)
        .build();
  }

}