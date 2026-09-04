package com.conecta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SenhaNaoConfereException extends RuntimeException {

	public SenhaNaoConfereException(String message) {
		super(message);
	}
}
