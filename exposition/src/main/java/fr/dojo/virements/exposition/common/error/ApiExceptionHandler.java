package fr.dojo.virements.exposition.common.error;

import fr.dojo.virements.application.common.exception.TechnicalException;
import fr.dojo.virements.domaine.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleBusinessException(BusinessException exception) {
        return new ApiError(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(TechnicalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiError handleTechnicalException(TechnicalException exception) {
        LOGGER.error("A technical error occurred while processing the request.", exception);
        return new ApiError(exception.getCode(), exception.getDescription());
    }
}
