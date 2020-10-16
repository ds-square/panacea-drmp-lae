package org.panacea.drmp.lae.domain.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LAEException extends RuntimeException {

    protected Throwable throwable;

    public LAEException(String message) {
        super(message);
    }

    public LAEException(String message, Throwable throwable) {
        super(message);
        this.throwable = throwable;
        log.error("[LAE]: ", message);
    }

    public Throwable getCause() {
        return throwable;
    }
}