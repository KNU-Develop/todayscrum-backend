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

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_name")
    private ToolName tool;

    private String email;

    public UserTool(User user, ToolName tool, String email) {
        this.user = user;
        this.tool = tool;
        this.email = email;
    }
}