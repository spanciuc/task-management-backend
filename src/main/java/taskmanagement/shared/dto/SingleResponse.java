package taskmanagement.shared.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class SingleResponse<T> {
    T data;
}
