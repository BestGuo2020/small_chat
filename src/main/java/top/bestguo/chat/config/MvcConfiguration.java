package top.bestguo.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import top.bestguo.chat.interceptor.LoginInterceptor;
import top.bestguo.chat.mapper.FriendMapper;
import top.bestguo.chat.mapper.UserMapper;
import top.bestguo.chat.service.UserService;
import top.bestguo.chat.websocket.MainEndpoint;

@Configuration(proxyBeanMethods = false)
public class MvcConfiguration implements WebMvcConfigurer {

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/signin*", "/signup*");
    }

    /**
     * 配置 ServerEndpointExporter 类，目的找到那些被标记的。
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    private void setUserMapper(UserMapper userMapper, FriendMapper friendMapper) {
        MainEndpoint.friendMapper = friendMapper;
        MainEndpoint.userMapper = userMapper;
    }

}
