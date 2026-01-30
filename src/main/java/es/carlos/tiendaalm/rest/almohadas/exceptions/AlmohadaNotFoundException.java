package es.carlos.tiendaalm.rest.almohadas.exceptions;

import es.almohadas.models.Tacto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlmohadaNotFoundException extends AlmohadaException {
    public AlmohadaNotFoundException(Long id) { super("Almohada con id " + id + " no encontrada" ); }
    public AlmohadaNotFoundException(Tacto tacto) { super("Almohada con tacto \"" + tacto + "\" no encontrada" ); }
}
