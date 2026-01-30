package es.carlos.tiendaalm.config.websockets;

import java.io.IOException;

public interface WebSocketSender {
    void sendMessage(String message) throws IOException;

    void sendPeriodicMessage() throws IOException;
}
