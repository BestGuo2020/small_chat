package top.bestguo.chat.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import top.bestguo.chat.msg.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录拦截器类
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Object user = request.getSession().getAttribute("user");
        if(user == null) {
            response.setContentType("text/html;charset=utf-8");
            // 如果是 ajax 请求响应头会有 x-requested-with
            if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
                ObjectMapper objectMapper = new ObjectMapper();
                // 消息类
                Message message = new Message();
                message.setStatus(Message.SESSION_EXPIRED);
                message.setMsg("登录已过期，请重新登录");
                // 返回对象
                String s = objectMapper.writeValueAsString(message);
                // 设置返回的文本类型
            } else {
                //非ajax请求时，session失效的处理
                String html = "<script>" +
                        "sessionStorage.removeItem('friend');" +
                        "alert('登录已失效，请重新登录');location.href = '/signin'" +
                        "</script>";
                response.getWriter().write(html);
            }
            return false;
        }
        return true;
    }
}
