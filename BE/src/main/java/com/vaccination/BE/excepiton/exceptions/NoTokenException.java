package com.vaccination.BE.excepiton.exceptions;

public class NoTokenException extends RuntimeException{
    private String message;

    public NoTokenException(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
