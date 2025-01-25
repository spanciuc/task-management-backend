package taskmanagement.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import taskmanagement.domain.user.config.UserConfig;

@Value
public class RegisterUserRequest {
    @NotNull
    @Pattern(regexp = UserConfig.USERNAME_REGEX,
            message = "{validation.user.username}")
    String username;

    @NotNull
    @Pattern(regexp = UserConfig.PASSWORD_REGEX,
            message = "{validation.user.password}")
    String password;

    @NotBlank
    @Email
    String email;
}
