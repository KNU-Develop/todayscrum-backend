package knu.kproject.entity.board;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    private Project project;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;


    @Column(nullable = false)
    private CATEGORY category;

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

}
