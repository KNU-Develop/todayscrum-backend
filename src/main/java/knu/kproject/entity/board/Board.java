package knu.kproject.entity.board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.dto.board.InputBoardDto;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.user.User;
import knu.kproject.global.CATEGORY;
import knu.kproject.global.PROGRESS;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BOARD")
public class Board {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Project project;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CATEGORY category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PROGRESS progress;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Master> master;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    public Board(User user, Project project, InputBoardDto dto) {
        this.title = dto.getTitle();
        this.project = project;
        this.user = user;
        this.content = dto.getContent();
        this.category = dto.getCategory();
        this.progress = dto.getProgress();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public void update(InputBoardDto dto, List<Master> masters) {
        this.setTitle(dto.getTitle() == null ? this.getTitle() : dto.getTitle());
        this.setContent(dto.getContent() == null ? this.getContent() : dto.getContent());
        this.setCategory(dto.getCategory() == null ? this.getCategory() : dto.getCategory());
        this.setProgress(dto.getProgress() == null ? this.getProgress() : dto.getProgress());
        this.setMaster(dto.getMastersId() == null ? this.getMaster() : masters);
    }
}
