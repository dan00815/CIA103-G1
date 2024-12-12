package com.event.cia103g1springboot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<LoginFilter> myFilter() {
		FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<LoginFilter>();
		registrationBean.setFilter(new LoginFilter());
		registrationBean.addUrlPatterns("/mem/profile","/shop/checkOut"); // 需要過濾的路徑，路徑要對到controller的映射
//		
		registrationBean.setOrder(1); // 設置加載順序，值越小優先級越高
		return registrationBean;
	}

}
