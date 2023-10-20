package com.nexos.NexosPruebaTecnica.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nexos.NexosPruebaTecnica.interceptor.SecurityInterceptor;

@Component
public class InterceptorConfiguration implements WebMvcConfigurer {

	@Value("${jwt.token.config.security.enabled:false}")
	private boolean isEnableSecurity;

	@Autowired
	private SecurityInterceptor securityInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (isEnableSecurity) {
			registry.addInterceptor(securityInterceptor);
		}
	}
}
