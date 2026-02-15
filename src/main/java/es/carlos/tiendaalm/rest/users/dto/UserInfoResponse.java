package es.carlos.tiendaalm.rest.users.dto;

import es.carlos.tiendaalm.rest.users.models.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
  private Long id;
  private String nombre;
  private String apellidos;
  private String username;
  private String email;
  @Builder.Default
  private Set<Role> roles = Set.of(Role.USER);
  @Builder.Default
  private Boolean isDeleted = false;
  @Builder.Default
  private List<Long> almohadas = new ArrayList<>();
}
