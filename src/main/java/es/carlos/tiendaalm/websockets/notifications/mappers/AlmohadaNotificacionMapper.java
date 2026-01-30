package es.carlos.tiendaalm.websockets.notifications.mappers;

import es.carlos.tiendaalm.rest.almohadas.models.*;
import es.carlos.tiendaalm.websockets.notifications.dto.AlmohadaNotificacionResponse;
import org.springframework.stereotype.Component;

@Component
public class AlmohadaNotificacionMapper {
    public AlmohadaNotificacionResponse toAlmohadaNotificacionDto(Almohada almohada){
        return new AlmohadaNotificacionResponse(
                almohada.getId(),
                almohada.getPeso(),
                almohada.getAltura(),
                almohada.getAncho(),
                almohada.getGrosor(),
                almohada.getTacto(),
                almohada.getFirmeza()
        );
    }
}
