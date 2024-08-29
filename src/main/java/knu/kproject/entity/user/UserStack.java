package knu.kproject.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "USER_STACK")
@NoArgsConstructor
@AllArgsConstructor
public class UserStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stack_id")
    private Stack stack;

    public UserStack(User user, Stack stack) {
        this.user = user;
        this.stack = stack;
    }
}
