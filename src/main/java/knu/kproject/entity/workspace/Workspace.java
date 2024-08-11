package knu.kproject.entity.workspace;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.entity.project.Project;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "workspace")
    @JsonManagedReference
    private List<Project> projects;
}
