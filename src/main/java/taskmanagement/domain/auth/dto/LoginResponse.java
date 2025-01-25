package taskmanagement.domain.auth.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class LoginResponse {
    String token;
}
