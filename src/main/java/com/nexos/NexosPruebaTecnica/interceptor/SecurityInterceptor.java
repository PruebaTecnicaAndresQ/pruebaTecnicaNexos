package com.nexos.NexosPruebaTecnica.interceptor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.services.JwtTokenWorker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtTokenWorker jwtTokenWorker;

	@Value("${jwt.token.config.token.auth.path}")
	private String authPath;

	@Value("${jwt.token.config.token.type}")
	private String tokenType;

	@Value("#{'${jwt.token.config.excluded.path}'.split(',')}")
	private List<String> excludePath;

	/**
	 * Metodo para realizar la validacion de token de seguridad en las peticiones
	 * REST
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean isValid = false;
		String path = request.getRequestURI();
		String method = request.getMethod();
		if (isExcluded(path)) {
			isValid = true;
		}
		if ("OPTIONS".equals(method)) {
			isValid = true;
		}
		if (!isValid && request.getHeader("authorization") != null && !request.getHeader("authorization").isEmpty()) {
			String token = request.getHeader("authorization").replace(tokenType + " ", "");
			isValid = !jwtTokenWorker.validateToken(token);
		}
		if (!isValid) {

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USUARIO NO AUTORIZADO");
		}
		return isValid;
	}

	/***
	 * Metodo que se encarga de saber que rutas NO debe validar la expiracion del
	 * token
	 * 
	 * @param incomingPath
	 * @return
	 * @throws ApiErrorException
	 */
	private boolean isExcluded(String incomingPath) throws ApiErrorException {
		boolean result = false;
		try {
			if (!incomingPath.isEmpty()) {
				if (incomingPath.equals(authPath)) {
					result = true;
				} else {
					excludePath.add("/error");
					for (String excluded : excludePath) {
						if (excluded.equals(incomingPath)) {
							result = true;
						}
					}
					if (incomingPath.contains("swagger") || incomingPath.contains("api-docs")) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			throw new ApiErrorException("InterceptorValidateJWToken=>isExcluded::Exception = " + e.getMessage());
		}
		return result;
	}
}
