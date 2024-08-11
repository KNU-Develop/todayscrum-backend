package knu.kproject.entity.project;

import jakarta.persistence.*;
import knu.kproject.entity.user.User;
import knu.kproject.global.ROLE;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_users")
public class ProjectUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Project project;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column(nullable = false)
    private ROLE role;

    @Column()
    private String color;
}
