package com.admission.interceptor;

import com.admission.config.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行登录接口
        String path = request.getRequestURI();
        if (path.contains("/user/login")) {
            return true;
        }

        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 Authorization header 获取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtils.parseToken(token);
            // 将用户信息放入 request attribute，方便后续使用
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("role", claims.get("role", String.class));
            return true;
        } catch (Exception e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期，请重新登录\"}");
            return false;
        }
    }
}
