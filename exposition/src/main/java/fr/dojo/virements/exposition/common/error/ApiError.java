package fr.dojo.virements.exposition.common.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error returned by the API")
public record ApiError(
        @Schema(description = "Stable error code", example = "err.fun.02") String code,
        @Schema(description = "Human-readable error message",
                example = "The amount must be strictly positive.") String message,
        @Schema(description = "Error label followed by the message",
                example = "Invalid transfer. The amount must be strictly positive.") String description) {
    public static ApiError withLabel(String code, String label, String message) {
        return new ApiError(code, message, label + ". " + message);
    }
}
