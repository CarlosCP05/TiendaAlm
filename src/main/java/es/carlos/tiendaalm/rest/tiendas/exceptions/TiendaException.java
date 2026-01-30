package es.carlos.tiendaalm.rest.tiendas.exceptions;

public abstract class TiendaException extends RuntimeException {
  public TiendaException(String message) {
    super(message);
  }
}
