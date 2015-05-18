package com.jrgoogledata;

/**
 *
 * @author guy
 */
public class JrIOError extends RuntimeException {

    public JrIOError() {
    }

    public JrIOError(String message) {
        super(message);
    }

    public JrIOError(String message, Throwable cause) {
        super(message, cause);
    }

    public JrIOError(Throwable cause) {
        super(cause);
    }

    public JrIOError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
