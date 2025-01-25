package taskmanagement.domain.user.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import taskmanagement.shared.persistence.BaseEntity;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserAccount extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
}
