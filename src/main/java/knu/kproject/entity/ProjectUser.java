package knu.kproject.entity;

import jakarta.persistence.*;
import knu.kproject.global.ROLE;
import lombok.*;

@Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "project_users")
public class ProjectUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Project project;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column(nullable = false)
    private ROLE role;

    @Column(nullable = false)
    private String color;
}
