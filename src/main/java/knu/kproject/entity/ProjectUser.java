package knu.kproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
