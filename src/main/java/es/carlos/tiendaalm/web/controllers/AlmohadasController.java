package es.carlos.tiendaalm.web.controllers;

import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.almohadas.services.AlmohadasService;
import es.carlos.tiendaalm.rest.users.models.User;
import es.carlos.tiendaalm.rest.users.services.UsersService;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class AlmohadasController {
  private final AlmohadasService almohadasService;
  private final UsersService usersService;

  @GetMapping("/misalmohadas")
  public String misAlmohadas(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> usuario = usersService.findByUsername(username);
    List<Almohada> almohadas = List.of();
    if (usuario.isPresent()) {
      almohadas = almohadasService.buscarPorUsuarioId(usuario.get().getId());
    }
    model.addAttribute("almohadas", almohadas);
    return "app/almohadas/lista";
  }

  @GetMapping("/misalmohadas/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Almohada almohada = almohadasService.buscarPorId(id).orElse(null);
    model.addAttribute("almohada", almohada);
    return "app/almohadas/detalle";
  }

}
