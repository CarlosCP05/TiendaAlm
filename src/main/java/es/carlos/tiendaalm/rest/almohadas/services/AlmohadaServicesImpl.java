package es.carlos.tiendaalm.rest.almohadas.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.exceptions.AlmohadaBadRequestException;
import es.carlos.tiendaalm.rest.almohadas.exceptions.AlmohadaNotFoundException;
import es.carlos.tiendaalm.rest.almohadas.mappers.AlmohadaMapper;
import es.carlos.tiendaalm.rest.almohadas.models.*;
import es.carlos.tiendaalm.rest.almohadas.repositories.AlmohadasRepository;
import es.carlos.tiendaalm.config.websockets.WebSocketConfig;
import es.carlos.tiendaalm.config.websockets.WebSocketHandler;
import es.carlos.tiendaalm.websockets.notifications.dto.AlmohadaNotificacionResponse;
import es.carlos.tiendaalm.websockets.notifications.mappers.AlmohadaNotificacionMapper;
import es.carlos.tiendaalm.websockets.notifications.models.Notificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@CacheConfig(cacheNames = {"almohadas"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlmohadaServicesImpl implements AlmohadasService, InitializingBean {
    private final AlmohadasRepository almohadasRepository;
    private final AlmohadaMapper almohadaMapper;
    //*Si creo la entidad tienda* private final Tienda tienda;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlmohadaNotificacionMapper almohadaNotificacionMapper;
    private WebSocketHandler webSocketHandler;

    public void afterPropertiesSet() { this.webSocketHandler = this.webSocketConfig.webSocketAlmohadasHandler(); }

    //Para que se pueda inicializar en los test
    public void setWebSocketServicesSet(WebSocketHandler webSocketHandler) { this.webSocketHandler = webSocketHandler; }

    @Override
    public Page<AlmohadaResponseDto> findAll(Optional<String> firmeza, Optional<Tacto> tacto, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando almohada por firmeza: {}, tacto: {}, isDeleted: {}", firmeza, tacto, isDeleted);

        //Búsqueda por firmeza
        Specification<Almohada> specFirmezaAlmohada = (root, query, criteriaBuilder) ->
            firmeza.map(f -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firmeza")), "%" + f.toLowerCase() + "%")).
                orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        //Búsqueda por tacto
        Specification<Almohada> specTactoAlmohada = (root, query, criteriaBuilder) ->
            tacto.map(t -> criteriaBuilder.like(criteriaBuilder.upper(root.get("tacto")), "%" + t + "%")).
                orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        //Búsqueda por isDeleted
        Specification<Almohada> specIsDeleted = (root, query, criteriaBuilder) ->
            isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Almohada> criterio = Specification.allOf(specFirmezaAlmohada, specTactoAlmohada, specIsDeleted);

        return almohadasRepository.findAll(criterio, pageable).map(almohadaMapper::toAlmohadaResponseDto);
    }

    @Cacheable(key = "#id")
    @Override
    public AlmohadaResponseDto findById(Long id) {
        log.info("Buscando almohada por id: {} ", id);

        return almohadaMapper.toAlmohadaResponseDto(almohadasRepository.findById(id).orElseThrow(() -> new AlmohadaNotFoundException(id)));
    }

    @Override
    public Page<AlmohadaResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Obteniendo almohadas del usuario con id: {}", usuarioId);
        return almohadasRepository.findByUsuarioId(usuarioId, pageable)
                .map(almohadaMapper::toAlmohadaResponseDto);
    }

    @Override
    public AlmohadaResponseDto findByUsuarioId(Long usuarioId, Long almohadaId) {
        log.info("Obteniendo tarjetas del usuario con id: {}", usuarioId);
        var almohadas = almohadasRepository.findByUsuarioId(usuarioId);
        var almohadaEncontrada = almohadas.stream().filter(t ->  t.getId().equals(almohadaId))
                .findFirst().orElse(null);
        if (almohadaEncontrada == null) {
            throw new AlmohadaBadRequestException("La almohada " + almohadaId + " no corresponde a este usuario");
        }
        return almohadaMapper.toAlmohadaResponseDto(almohadaEncontrada);
    }

    //Poner un checkStock cuando este creado


    @CachePut(key = "#result.id")
    @Override
    public AlmohadaResponseDto save(AlmohadaCreateDto almohadaCreateDto) {
        log.info("Guardando almohada: {}", almohadaCreateDto);
        Almohada almohadaSaved = almohadasRepository.save(almohadaMapper.toAlmohada(almohadaCreateDto));
        onChange(Notificacion.Tipo.CREATE, almohadaSaved);
        return almohadaMapper.toAlmohadaResponseDto(almohadaSaved);
    }

    @Override
    public AlmohadaResponseDto save(AlmohadaCreateDto almohadaCreateDto, Long usuarioId) {
        log.info("Guardando tarjeta: {} de usuarioId: {}", almohadaCreateDto, usuarioId);
        /*  Para cuando este creado Stock, modificar el save anterior tambien
        Stock stock = checkStock(almohadaCreateDto.getStock());
        var usuario = stock.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new TarjetaBadRequestException("El usuario no se corresponde con el titular");
        }*/
        Almohada almohadaSaved = almohadasRepository.save(
                almohadaMapper.toAlmohada(almohadaCreateDto));
        onChange(Notificacion.Tipo.CREATE, almohadaSaved);
        return almohadaMapper.toAlmohadaResponseDto(almohadaSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlmohadaResponseDto update(Long id, AlmohadaUpdateDto almohadaUpdateDto) {
        log.info("Actualizando almohada por id: {}", id);
        var almohadaActual = almohadasRepository.findById(id).orElseThrow(() -> new AlmohadaNotFoundException(id));
        Almohada almohadaUpdate = almohadasRepository.save(almohadaMapper.toAlmohada(almohadaUpdateDto, almohadaActual));
        onChange(Notificacion.Tipo.UPDATE, almohadaUpdate);
        return almohadaMapper.toAlmohadaResponseDto(almohadaActual);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlmohadaResponseDto update(Long id, AlmohadaUpdateDto almohadaUpdateDto, Long usuarioId) {
        log.info("Actualizando tarjeta por id: {}", id);
        var almohadaActual = almohadasRepository.findById(id).orElseThrow(()-> new AlmohadaNotFoundException(id));
        /*  Para cuando este creado Stock, modificar el update anterior tambien
        var usuario = almohadaActual.getStock().getTienda().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlmohadaBadRequestException("La almohada " +
                    almohadaUpdateDto.getId() + " no corresponde a este usuario");
        }*/
        Almohada almohadaUpdate =  almohadasRepository.save(
                almohadaMapper.toAlmohada(almohadaUpdateDto, almohadaActual));
        onChange(Notificacion.Tipo.UPDATE, almohadaUpdate);
        return almohadaMapper.toAlmohadaResponseDto(almohadaUpdate);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando almohada por id: {}", id);
        Almohada almohadaDeleted = almohadasRepository.findById(id).orElseThrow(() -> new AlmohadaNotFoundException(id));
        almohadasRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, almohadaDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        log.debug("Borrando almohada por id: {}", id);
        Almohada almohadaDeleted = almohadasRepository.findById(id).orElseThrow(()-> new AlmohadaNotFoundException(id));
        /*  Para cuando este creado Stock, modificar el deleted anterior tambien
        var usuario = almohadaDeleted.getStock().getTienda().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlmohadaBadRequestException("La almohada " + id + " no corresponde a este usuario");
        }*/
        almohadasRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, almohadaDeleted);

    }

    void onChange(Notificacion.Tipo tipo, Almohada data) {
        log.debug("Servicio de productos onChange con tipo: {}, y datos: {}", tipo, data);
         if (webSocketHandler == null) {
             log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
             webSocketHandler = this.webSocketConfig.webSocketAlmohadasHandler();
         }

         try {
             Notificacion<AlmohadaNotificacionResponse> notificacion = new Notificacion<>(
                "ALMOHADAS",
                tipo,
                almohadaNotificacionMapper.toAlmohadaNotificacionDto(data),
                LocalDateTime.now().toString()
             );

            String json = objectMapper.writeValueAsString(notificacion);

            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketHandler.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebSocketAlmohadas-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
         } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
         }
    }
}
