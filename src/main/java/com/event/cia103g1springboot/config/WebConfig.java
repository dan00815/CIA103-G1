//package com.event.cia103g1springboot.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web MVC 配置類
// * 用於設置視圖控制器
// */
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    /**
//     * 添加視圖控制器
//     * @param registry 視圖控制器註冊物件
//     */
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // 將請求路徑與視圖名稱對應，無需編寫控制器方法
//
//        // 將 /emp/login 路徑對應到 emp/login 視圖
//        registry.addViewController("/emp/login").setViewName("emp/login");
//
//        // 將 /emp/register 路徑對應到 emp/register 視圖
//        registry.addViewController("/emp/register").setViewName("emp/register");
//
//        // 將 /emp/reset-password 路徑對應到 emp/reset_password 視圖
//        registry.addViewController("/emp/reset-password").setViewName("emp/reset_password");
//
//        // 將 /emp/list 路徑對應到 emp/list 視圖
//        registry.addViewController("/emp/list").setViewName("emp/list");
//    }
//}
