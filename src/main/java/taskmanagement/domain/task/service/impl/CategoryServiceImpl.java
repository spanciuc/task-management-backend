package taskmanagement.domain.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import taskmanagement.domain.task.dto.CategoryDTO;
import taskmanagement.domain.task.dto.CategoryRequest;
import taskmanagement.domain.task.mapper.CategoryMapper;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.domain.task.persistence.CategoryRepository;
import taskmanagement.domain.task.service.CategoryService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::map).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO create(CategoryRequest request) {
        Category category = categoryMapper.map(request);
        categoryRepository.save(category);
        return categoryMapper.map(category);
    }

    @Override
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }
}
