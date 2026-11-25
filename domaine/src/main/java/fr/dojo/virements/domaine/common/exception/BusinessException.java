package fr.dojo.virements.domaine.common.exception;

public abstract class BusinessException extends RuntimeException {
    private final BusinessError error;

    protected BusinessException(BusinessError error, String message) {
        super(message);
        this.error = error;
    }

    public String getCode() {
        return error.code();
    }

    public String getLabel() {
        return error.label();
    }
}
