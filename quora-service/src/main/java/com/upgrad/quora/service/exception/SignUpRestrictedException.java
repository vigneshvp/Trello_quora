package com.upgrad.quora.service.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * SignUpRestrictedException is thrown when a user is restricted to register in the application due to repeated username
 * or email.
 */
public class SignUpRestrictedException extends Exception {
    private static final long serialVersionUID = 5165750165173548857L;
    private final String code;
    private final String errorMessage;

    public SignUpRestrictedException(final String code, final String errorMessage) {
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

