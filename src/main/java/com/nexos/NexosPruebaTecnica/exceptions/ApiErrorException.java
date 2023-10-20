package com.nexos.NexosPruebaTecnica.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiErrorException extends Exception {

	private static final long serialVersionUID = -4277522348170437215L;

	public ApiErrorException(String message) {
		super(message);
	}

}
