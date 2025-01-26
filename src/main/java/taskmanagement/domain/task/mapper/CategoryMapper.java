package taskmanagement.domain.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import taskmanagement.domain.task.dto.CategoryDTO;
import taskmanagement.domain.task.dto.CategoryRequest;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.shared.mapper.OptionalMapper;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class})
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Category map(CategoryRequest request);

    CategoryDTO map(Category task);

    default String map(Optional<String> value) {
        return value.orElse(null);
    }
}
