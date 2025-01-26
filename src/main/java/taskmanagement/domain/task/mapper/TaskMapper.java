package taskmanagement.domain.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import taskmanagement.domain.task.dto.TaskDTO;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.domain.task.persistence.Task;
import taskmanagement.shared.mapper.OptionalMapper;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "category", source = "category")
    Task map(TaskRequest request, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "category", source = "category")
    void map(@MappingTarget Task task, TaskRequest request, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    Task map(TaskRequest request);

    TaskDTO map(Task task);

}
