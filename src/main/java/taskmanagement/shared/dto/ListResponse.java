package taskmanagement.shared.dto;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class ListResponse<T> {
    List<T> data;
}
