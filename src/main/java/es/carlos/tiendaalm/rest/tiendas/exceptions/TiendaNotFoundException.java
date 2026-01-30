package es.carlos.tiendaalm.rest.tiendas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de tienda no encontrado
 * Status 404
*/

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TiendaNotFoundException extends TiendaException {
  public TiendaNotFoundException(Long id) {
    super("Tienda con id " + id + " no encontrado");
  }

  public TiendaNotFoundException(String tienda) {
    super("Tienda " + tienda + " no encontrado");
  }
}
