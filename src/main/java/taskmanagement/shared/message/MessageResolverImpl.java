package taskmanagement.shared.message;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;

@RequiredArgsConstructor
@Component
@Validated
class MessageResolverImpl implements MessageResolver {

    private final MessageSource messageSource;

    @Override
    public String resolve(@NotNull MessageCode messageCode, @NotNull Locale locale) {
        return messageSource.getMessage(messageCode.getCode(), null, messageCode.getCode(), locale);
    }
}
