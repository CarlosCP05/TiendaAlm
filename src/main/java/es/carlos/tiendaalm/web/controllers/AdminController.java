package es.carlos.tiendaalm.web.controllers;

import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.almohadas.services.AlmohadasService;
import java.util.Optional;
import java.util.UUID;

import es.carlos.tiendaalm.web.services.I18nService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final AlmohadasService almohadasService;
  private final I18nService i18nService;


  @GetMapping("/almohadas")
  public String almohadas(Model model,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<AlmohadaResponseDto> almohadasPage = almohadasService.findAll(
      Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", almohadasPage);
    return "admin/almohadas/lista";
  }

  @GetMapping("/almohadas/filter")
  public String almohadasFiltrar(Model model,
                         @RequestParam(required = false) Optional<String> numero,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<AlmohadaResponseDto> almohadasPage = almohadasService.findAll(
      numero, Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", almohadasPage);
    return "fragments/listaAlmohadas";
  }

  @GetMapping("/almohadas/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Almohada almohada = almohadasService.buscarPorId(id).orElse(null);
    model.addAttribute("almohada", almohada);
    return "admin/almohadas/detalle";
  }

  @GetMapping("/almohadas/new")
  public String nuevaAlmohadaForm(Model model) {
    model.addAttribute("almohada", AlmohadaCreateDto.builder().build());
    model.addAttribute("modoEditar", false );
    return "admin/almohadas/form";
  }

  @PostMapping("/almohadas/new")
  public String nuevaAlmohadaSubmit(@Valid @ModelAttribute("almohada") AlmohadaCreateDto almohada,
                                    BindingResult bindingResult) {

    log.info("Datos recibidos del formulario: {}", almohada);
    if (bindingResult.hasErrors()) {
      log.info("hay errores en la validación");
      return "admin/almohadas/form";
    } else {
      almohadasService.save(almohada);
      return "redirect:/admin/almohadas";
    }
  }

  @GetMapping("/almohadas/{id}/edit")
  public String editarAlmohadaForm(@PathVariable Long id, Model model) {
    Almohada almohadaEncontrada = almohadasService.buscarPorId(id).orElse(null);
    if (almohadaEncontrada == null) {
      return "redirect:/admin/almohadas/new";
    } else {
      AlmohadaUpdateDto almohada = AlmohadaUpdateDto.builder()
          .peso(almohadaEncontrada.getPeso())
          .altura(almohadaEncontrada.getAltura())
          .ancho(almohadaEncontrada.getAncho())
          .grosor(almohadaEncontrada.getGrosor())
          .tacto(almohadaEncontrada.getTacto())
          .firmeza(almohadaEncontrada.getFirmeza())
          .build();
      model.addAttribute("almohada", almohada);
      model.addAttribute("almohadaId", id);
      model.addAttribute("modoEditar", true);
      return "admin/almohadas/form";
    }
  }

  @PostMapping("/almohadas/{id}/edit")
  public String editarAlmohadaSubmit(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("almohada") AlmohadaUpdateDto almohada,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      redirectAttributes.addFlashAttribute("error",
          "Ha ocurrido un error al actualizar la almohada.");
      model.addAttribute("almohadaId", id );
      model.addAttribute("modoEditar", true);
      return "admin/almohadas/form";
    }

    almohadasService.update(id, almohada);
    redirectAttributes.addFlashAttribute("success",
        "Almohada actualizada correctamente.");
    return "redirect:/admin/almohadas/{id}";
  }


  @PostMapping("/almohadas/{id}/delete")
  public String borrarAlmohada(@PathVariable Long id,
                              @RequestParam("deleteToken") String deleteToken,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
    String sessionKey = "deleteToken_" + id;
    String tokenInSession = (String) session.getAttribute(sessionKey);

    if (tokenInSession == null || !tokenInSession.equals(deleteToken)) {
      redirectAttributes.addFlashAttribute("error", "Confirmación inválida o caducada.");
      return "redirect:/admin/almohadas";
    }

    session.removeAttribute(sessionKey);
    almohadasService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Almohada borrada correctamente.");
    return "redirect:/admin/almohadas";
  }

  @GetMapping("/almohadas/{id}/delete/confirm")
  public String showModalBorrar(@PathVariable("id") Long id, Model model, HttpSession session) {
    Optional<Almohada> almohada = almohadasService.buscarPorId(id);
    String deleteMessage;
    if (almohada.isPresent()) {
      deleteMessage = i18nService.getMessage("almohadas.borrar.mensaje",
        new Object[]{almohada.get().getId()} );
    } else {
      return "redirect:/almohadas/?error=true";
    }

    String token = UUID.randomUUID().toString();
    String sessionKey = "deleteToken_" + id;
    session.setAttribute(sessionKey, token);

    model.addAttribute("deleteUrl", "/admin/almohadas/" + id + "/delete");
    model.addAttribute("deleteToken", token);
    model.addAttribute("deleteTitle",
      i18nService.getMessage("almohadas.borrar.titulo")
    );
    model.addAttribute("deleteMessage", deleteMessage);
    return "fragments/deleteModal";
  }
}
