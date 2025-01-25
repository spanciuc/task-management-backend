package taskmanagement.shared.message;

import jakarta.validation.constraints.NotNull;

import java.util.Locale;

public interface MessageResolver {
    String resolve(@NotNull MessageCode messageCode, @NotNull Locale locale);
}
