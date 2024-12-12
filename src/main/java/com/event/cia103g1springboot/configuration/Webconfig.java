package com.event.cia103g1springboot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/event/addpage").setViewName("back-end/evtaddpage");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/listpage").setViewName("front-end/listpage");
        registry.addViewController("/attend").setViewName("front-end/attendpage");
        registry.addViewController("/sucessandfail").setViewName("back-end/evt/sucessandfail");
//        registry.addViewController("/sucessattend").setViewName("back-end/attendsucess");
        registry.addViewController("/planroom/add").setViewName("plan/planroom/addpage");
        registry.addViewController("/ezpay").setViewName("/back-end/ezpay");
//        registry.addViewController("/product/productlist").setViewName("front-end/product/productlist");

    }
}
