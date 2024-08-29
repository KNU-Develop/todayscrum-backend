package knu.kproject.entity.project;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.workspace.Workspace;
import lombok.*;
import org.hibernate.jdbc.Work;

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

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Board> boards;

    @Column
    private String color;

    public Project(PutProjectDto dto, Workspace workspace) {
        this.workspace = workspace;
        this.title = dto.getTitle();
        this.overview = dto.getOverview();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.color = dto.getColor();
    }

    public void update(PutProjectDto dto) {
        this.setTitle(dto.getTitle() == null ? this.getTitle() : dto.getTitle().trim());
        this.setOverview(dto.getOverview() == null ? this.getOverview() : dto.getOverview().trim());
        this.setStartDate(dto.getStartDate() == null ? this.getStartDate() : dto.getStartDate());
        this.setEndDate(dto.getEndDate() == null ? this.getEndDate() : dto.getEndDate());
        this.setColor(dto.getColor() == null ? this.getColor() : dto.getColor().trim());
    }
}
