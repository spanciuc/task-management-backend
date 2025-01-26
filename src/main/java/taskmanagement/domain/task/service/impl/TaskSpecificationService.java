package taskmanagement.domain.task.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskmanagement.domain.task.dto.TaskFilter;
import taskmanagement.domain.task.persistence.Task;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskSpecificationService {
    public Specification<Task> toSpecification(TaskFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.getCategories()
                    .filter(f -> !f.isEmpty())
                    .map(f -> root.get("category").get("id").in(f))
                    .ifPresent(predicates::add);

            filter.getStatuses()
                    .filter(f -> !f.isEmpty())
                    .map(f -> root.get("status").in(f))
                    .ifPresent(predicates::add);

            filter.getPriorities()
                    .filter(f -> !f.isEmpty())
                    .map(f -> root.get("priority").in(f))
                    .ifPresent(predicates::add);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
