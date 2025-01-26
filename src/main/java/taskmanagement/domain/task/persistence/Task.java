package taskmanagement.domain.task.persistence;

import jakarta.persistence.*;
import lombok.*;
import taskmanagement.domain.task.config.TaskConfig;
import taskmanagement.shared.persistence.BaseEntity;

import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskConfig.Priority priority;
    @Enumerated(EnumType.STRING)
    private TaskConfig.Status status;
    private LocalDate dueDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
