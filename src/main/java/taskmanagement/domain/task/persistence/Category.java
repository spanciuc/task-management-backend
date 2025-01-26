package taskmanagement.domain.task.persistence;

import jakarta.persistence.*;
import lombok.*;
import taskmanagement.shared.persistence.BaseEntity;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category extends BaseEntity {
    @Column(nullable = false)
    private String title;
    private String description;

    @ToString.Exclude
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Task> tasks;
}
