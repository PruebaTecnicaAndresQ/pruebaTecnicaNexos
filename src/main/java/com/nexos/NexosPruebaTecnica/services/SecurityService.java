package com.nexos.NexosPruebaTecnica.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.nexos.NexosPruebaTecnica.dtos.JwtResponse;
import com.nexos.NexosPruebaTecnica.exceptions.ApiUnauthorizedException;
import com.nexos.NexosPruebaTecnica.utils.Constants;

@Service
public class SecurityService {

	@Autowired
	private JwtTokenWorker jwtTokenWorker;

	@Value("${jwt.token.config.default-password}")
	private String defaultPassword;

	@Value("${jwt.token.config.default-user-name}")
	private String defaultUserName;

	/***
	 * Metodo para obtener el token de seguridad de los servicios
	 * 
	 * @param paramMap
	 * @param grantType
	 * @return
	 * @throws Exception
	 */
	public JwtResponse getToken(MultiValueMap<String, String> paramMap, String grantType) throws Exception {

		try {
			validateInput(paramMap, grantType);
			if (defaultPassword.equals(paramMap.getFirst("client_secret"))
					&& defaultUserName.equals(paramMap.getFirst("client_id"))) {
				return jwtTokenWorker.buildToken(paramMap.getFirst("client_id"));
			} else {
				throw new ApiUnauthorizedException(Constants.USER_NOT_FOUND);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Metodo para validar la entrada de solicitud de token
	 * 
	 * @param paramMap
	 * @param grantType
	 * @return
	 * @throws ApiUnauthorizedException
	 * @throws ApiUnauthorizedException
	 */
	private boolean validateInput(MultiValueMap<String, String> paramMap, String grantType)
			throws ApiUnauthorizedException, ApiUnauthorizedException {
		try {
			if (grantType.isEmpty() || !grantType.equals(Constants.CLIENT_CREDENTIALS)) {
				throw new ApiUnauthorizedException(Constants.INVALID_GRANT_TYPE);
			}
			if (Objects.isNull(paramMap) || Objects.isNull(paramMap.getFirst("client_id"))
					|| paramMap.getFirst("client_id").isEmpty() || Objects.isNull(paramMap.getFirst("client_secret"))
					|| paramMap.getFirst("client_secret").isEmpty()) {
				throw new ApiUnauthorizedException(Constants.INVALID_PARAM_VALUES);
			}
		} catch (ApiUnauthorizedException e) {
			throw e;
		}
		return true;
	}
}
