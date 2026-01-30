package es.carlos.tiendaalm.config.websockets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Value("${API_VERSION:v1}")
    private String apiVersion;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAlmohadasHandler(), "/ws/" + apiVersion + "/almohadas");
    }

    @Bean
    public WebSocketHandler webSocketAlmohadasHandler() { return new WebSocketHandler("Almohadas"); }
}
