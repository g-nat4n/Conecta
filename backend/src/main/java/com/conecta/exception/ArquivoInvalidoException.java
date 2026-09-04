package com.conecta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArquivoInvalidoException extends RuntimeException {

	public ArquivoInvalidoException(String message) {
		super(message);
	}
}
