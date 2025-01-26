package taskmanagement.domain.task.service;

import taskmanagement.domain.task.dto.CategoryDTO;
import taskmanagement.domain.task.dto.CategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    /**
     * Gets all categories.
     *
     * @return list of categories.
     */
    List<CategoryDTO> getAll();

    /**
     * Creates a new category.
     *
     * @param request request.
     * @return The created category.
     */
    CategoryDTO create(CategoryRequest request);


    /**
     * Deletes a category by id.
     *
     * @param id the category id.
     */
    void delete(UUID id);
}
