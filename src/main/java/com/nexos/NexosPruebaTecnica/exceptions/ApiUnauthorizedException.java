package com.nexos.NexosPruebaTecnica.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ApiUnauthorizedException extends Exception {

	private static final long serialVersionUID = 1270146107444213565L;

	public ApiUnauthorizedException(String msg) {
		super(msg);
	}
}
