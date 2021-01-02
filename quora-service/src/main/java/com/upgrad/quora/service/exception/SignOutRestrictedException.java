package com.upgrad.quora.service.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * SignOutRestrictedException is thrown when a user is not signed in the application and tries to sign out of the
 * application.
 */
public class SignOutRestrictedException extends Exception {
    private static final long serialVersionUID = 1009518001108871222L;
    private final String code;
    private final String errorMessage;

    public SignOutRestrictedException(final String code, final String errorMessage) {
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
