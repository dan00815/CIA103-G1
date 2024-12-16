package com.event.cia103g1springboot.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("loginUser");
        
        // 檢查是否已登錄
        if (user == null) {
            // 如果是AJAX請求
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // 普通請求重定向到登錄頁
            response.sendRedirect("/emp/login");
            return false;
        }
        return true;
    }
}




//package com.example.config;
//
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//public class LoginInterceptor implements HandlerInterceptor {
//    
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        Object user = session.getAttribute("loginUser");
//        
//        // 檢查是否已登錄
//        if (user == null) {
//            // 如果是AJAX請求
//            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return false;
//            }
//            // 普通請求重定向到登錄頁
//            response.sendRedirect("/emp/login");
//            return false;
//        }
//        return true;
//    }
//}
