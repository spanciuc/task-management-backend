package taskmanagement.shared.message;

import lombok.Getter;

@Getter
public enum MessageCode {
    // Errors
    ERROR_AUTHENTICATION_REQUIRED("error.authentication_required"),
    ERROR_ACCESS_DENIED("error.access_denied"),
    ERROR_BAD_CREDENTIALS("error.bad_credentials"),
    ERROR_VALIDATION_FAILED("error.validation_failed"),
    ERROR_JWT_GENERATION_ERROR("error.jwt_generation_error"),
    ERROR_INVALID_AUTHENTICATION_TOKEN("error.authentication.token.invalid"),
    ERROR_RESOURCE_NOT_FOUND("error.not_found"),
    ERROR_INTERNAL_SERVER_ERROR("error.internal_server_error"),

    // User
    VALIDATION_USER_USERNAME_EXISTS("validation.user.username.exists"),
    VALIDATION_USER_USERNAME("validation.user.username"),
    VALIDATION_USER_PASSWORD("validation.user.password"),
    VALIDATION_USER_EMAIL("validation.user.email"),
    VALIDATION_USER_EMAIL_EXISTS("validation.user.email.exists"),

    // Category
    ERROR_CATEGORY_NOT_FOUND("error.category.not_found"),

    // Task validation
    ERROR_TASK_NOT_FOUND("error.task.not_found");

    private final String code;

    MessageCode(String code) {
        this.code = code;
    }

}
