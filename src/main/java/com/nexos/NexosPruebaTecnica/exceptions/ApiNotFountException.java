package com.nexos.NexosPruebaTecnica.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ApiNotFountException extends Exception {


	private static final long serialVersionUID = 2321452689491293015L;

	public ApiNotFountException(String message) {
		super(message);
	}
}
