package taskmanagement.shared.dto;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class IdResponse {
    UUID id;
}
