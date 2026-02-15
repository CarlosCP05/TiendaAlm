package es.carlos.tiendaalm.graphql.controllers;

import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.almohadas.repositories.AlmohadasRepository;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import es.carlos.tiendaalm.rest.tiendas.repositories.TiendasRepository;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

//Quiza cambiar la clase para añadir más stock o sustituir a tienda?
@RequiredArgsConstructor
@Controller
public class AlmohadaTiendaGraphQLController {
  private final AlmohadasRepository almohadasRepository;
  private final TiendasRepository tiendasRepository;

  @QueryMapping
  public List<Almohada> almohadas() {
    return almohadasRepository.findAll();
  }

  @QueryMapping
  public Almohada almohadaById(@Argument Long id) {
    Optional<Almohada> almohadaOpt = almohadasRepository.findById(id);
    return almohadaOpt.orElse(null);
  }

  @QueryMapping
  public List<Tienda> tiendas() {
    return tiendasRepository.findAll();
  }

  @QueryMapping
  public Tienda tiendaById(@Argument Long id) {
    return tiendasRepository.findById(id).orElse(null);
  }

  @QueryMapping
  public List<Tienda> tiendasByNombre(@Argument String nombre) {
    return tiendasRepository.findByNombreContainingIgnoreCase(nombre);
  }

/* Relaciones con stock, hay que cambiar?
  @SchemaMapping(typeName = "Almohada", field = "tienda")
  public Tienda tienda(Almohada almohada) {
    return almohada.getStock().getTienda();
  }
  @SchemaMapping(typeName = "Almohada", field = "tiendas")
  public List<Almohada> almohadas(Tienda tienda) {
    return almohadasRepository.findByStock(tienda)
  }
*/
}


