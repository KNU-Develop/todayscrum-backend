package knu.kproject.entity.project;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
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
    @JsonBackReference
    private Project project;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ROLE role;

    @Column()
    private String color;

    @Column
    private CHOICE choice;
}
