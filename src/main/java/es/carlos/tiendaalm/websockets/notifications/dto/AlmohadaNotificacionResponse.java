package es.carlos.tiendaalm.websockets.notifications.dto;

import es.carlos.tiendaalm.rest.almohadas.models.Tacto;

public record AlmohadaNotificacionResponse(
    Long id,
    Integer peso,
    Integer altura,
    Integer ancho,
    Integer grosor,
    Tacto tacto,
    String firmeza
){}
