package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "USER_TOOL")
public class UserTool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tool_id")
    private Tool tool;

    private String email;

    public UserTool(User user, Tool tool, String email) {
        this.user = user;
        this.tool = tool;
        this.email = email;
    }
}