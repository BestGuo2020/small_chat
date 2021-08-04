package top.bestguo.chat.config;

import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Map;

/**
 * 通过它，拿到 HttpSession
 */
public class HttpSessionConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取 HttpSession
        Object httpSession = request.getHttpSession();
        // 获取配置对象，将 HttpSession 保存到 Map 集合中
        Map<String, Object> userProperties = sec.getUserProperties();
        userProperties.put("httpSession", httpSession);
    }

}
