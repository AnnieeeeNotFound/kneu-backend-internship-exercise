// The one JSON shape every error comes back as: {"error": "...", "message": "..."}.
// Calls: nothing (plain data box). Used by: ApiExceptionHandler.
package com.kneu.medications.error;

/**
 * Error JSON body, e.g. {"error": "NOT_FOUND", "message": "..."}.
 */
public record ErrorResponse(String error, String message) {
}
