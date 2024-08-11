package knu.kproject.entity.project;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.workspace.Workspace;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Workspace workspace;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column
    private Timestamp startDate;

    @Column
    private Timestamp endDate;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Board> boards;

    @Column
    private String color;
}
