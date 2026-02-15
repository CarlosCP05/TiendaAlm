package es.carlos.tiendaalm.web.controllers;

import es.carlos.tiendaalm.rest.almohadas.dto.AlmohadaResponseDto;
import es.carlos.tiendaalm.rest.almohadas.services.AlmohadasService;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
  private final AlmohadasService almohadasService;


  @GetMapping({"", "/", "/index"})
  public String index(Model model,
                      @RequestParam(name = "page", defaultValue = "0") int page,
                      @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<AlmohadaResponseDto> almohadasPage = almohadasService.findAll(
        Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", almohadasPage);
    return "index";
  }

}
