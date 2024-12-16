package com.event.cia103g1springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置類
 * 設定攔截器和資源處理
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	LoginInterceptor loginInterceptor;
    /**
     * 添加攔截器
     * @param registry 攔截器註冊物件
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
        .addPathPatterns("/emp/*",
                        "/ordlistall/*",
                        "/event/*",
                        "/planord/listall",
                        "/planord/view/*")
        .excludePathPatterns(
                "/emp/login",        // 排除登入頁
                "/emp/register",     // 排除註冊頁
                "/emp/reset-password", // 排除重設密碼頁
                "/emp/base",
                "/event/calendar"// 排除基本頁
        );
    }

    /**
     * 設置靜態資源處理
     * @param registry 資源處理器註冊物件
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // 處理所有靜態資源請求
                .addResourceLocations("classpath:/static/") // 指定靜態資源目錄
                .setCachePeriod(0); // 設置緩存週期為0，代表不緩存
    }
}



//package com.event.cia103g1springboot.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web MVC 配置類
// * 設定攔截器和資源處理
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//    
//    /**
//     * 添加攔截器
//     * @param registry 攔截器註冊物件
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns( // 只攔截需要登入的路徑
//                        "/back/profile",             // 例如，員工個人資料頁
//                        "/emp/edit/**",             // 員工編輯頁
//                        "/emp/deactivate/**",       // 員工停用頁
//                        "/emp/list"                 // 員工列表頁
//                        // 你可以繼續添加更多需要攔截的路徑
//                );
//    }
//
//    /**
//     * 設置靜態資源處理
//     * @param registry 資源處理器註冊物件
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**") // 處理所有靜態資源請求
//                .addResourceLocations("classpath:/static/") // 指定靜態資源目錄
//                .setCachePeriod(0); // 設置緩存週期為0，代表不緩存
//    }
//}


//package com.example.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web MVC 配置類
// * 設定攔截器和資源處理
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//    
//    /**
//     * 添加攔截器
//     * @param registry 攔截器註冊物件
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**") // 攔截所有路徑
//                .excludePathPatterns(
//                        "/emp/login",                // 排除登入路徑
//                        "/emp/register",             // 排除註冊路徑
//                        "/emp/reset-password",       // 排除重設密碼路徑
//                        "/api/employee/login",       // 排除API登入路徑
//                        "/api/employee/register",    // 排除API註冊路徑
//                        "/api/employee/reset-password", // 排除API重設密碼路徑
//                        "/css/**",                   // 排除CSS資源
//                        "/js/**",                    // 排除JS資源
//                        "/images/**"                 // 排除圖片資源
//                );
//    }
//
//    /**
//     * 設置靜態資源處理
//     * @param registry 資源處理器註冊物件
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**") // 處理所有靜態資源請求
//                .addResourceLocations("classpath:/static/") // 指定靜態資源目錄
//                .setCachePeriod(0); // 設置緩存週期為0，代表不緩存
//    }
//}
