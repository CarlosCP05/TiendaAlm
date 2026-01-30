package es.carlos.tiendaalm.config.websockets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) { this.entity = entity; }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión establecida con el servidor");
        log.info("Sesión: {}", session);
        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - (App de Almohadas)");
        log.info("Servidor envía: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connexión cerrada con el servidor: {}", status);
        sessions.remove(session);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        log.info("Enviar mensaje de cambios en la entidad: {} : {}", entity, message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                log.info("Servidor WS envía: {}", message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void sendPeriodicMessage() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic messege " + LocalTime.now();
                log.info("Server sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // No hago nada con los mensajes que me llegan
        // Si quisiéramos un chat, por ejemplo, aquí lo gestionaríamos,
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() { return List.of("subprotocol.demo.websockets"); }
}
