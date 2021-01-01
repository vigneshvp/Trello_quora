package com.upgrad.quora.service.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * UserNotFoundException is thrown when the user is not found in the database.
 */
public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 5942111024196862253L;
    private final String code;
    private final String errorMessage;

    public UserNotFoundException(final String code, final String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
