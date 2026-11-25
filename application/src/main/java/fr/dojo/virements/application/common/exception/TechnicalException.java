package fr.dojo.virements.application.common.exception;

public final class TechnicalException extends RuntimeException {
    public static final String CODE = "err.tech.01";
    public static final String DESCRIPTION = "An unexpected technical error occurred.";

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return CODE;
    }

    public String getDescription() {
        return DESCRIPTION;
    }
}
