package taskmanagement.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanagement.domain.task.dto.CategoryDTO;
import taskmanagement.domain.task.dto.CategoryRequest;
import taskmanagement.domain.task.service.CategoryService;
import taskmanagement.shared.dto.IdResponse;
import taskmanagement.shared.dto.ListResponse;
import taskmanagement.shared.dto.SingleResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/categories")
@Tag(name = "Category controller")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get categories list endpoint")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).READ)")
    public ListResponse<CategoryDTO> getList() {
        List<CategoryDTO> categories = categoryService.getAll();
        return ListResponse.of(categories);
    }

    @Operation(summary = "Create category endpoint")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).WRITE)")
    public SingleResponse<IdResponse> create(@RequestBody @Valid CategoryRequest createRequest) {
        CategoryDTO category = categoryService.create(createRequest);
        return SingleResponse.of(IdResponse.of(category.getId()));
    }

    @Operation(summary = "Delete category endpoint")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).DELETE)")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }

}
