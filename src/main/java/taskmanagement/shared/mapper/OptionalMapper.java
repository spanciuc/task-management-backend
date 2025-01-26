package taskmanagement.shared.mapper;

import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public class OptionalMapper {
    public <T> T map(Optional<T> value) {
        return value.orElse(null);
    }
}
