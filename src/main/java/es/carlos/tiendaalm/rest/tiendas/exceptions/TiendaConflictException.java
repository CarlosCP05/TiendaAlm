package es.carlos.tiendaalm.rest.tiendas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de conflicto en titular
 * Status 409
*/
@ResponseStatus(HttpStatus.CONFLICT)
public class TiendaConflictException extends TiendaException {

  public TiendaConflictException(String message) {
    super(message);
  }
}