package knu.kproject.entity.comment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.dto.comment.InputCommentDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.user.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMMENT")
public class Comment {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Board board;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Master> masters;

    public Comment(User user, Board board, InputCommentDto dto) {
        this.description = dto.getDescription();
        this.board = board;
        this.user = user;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public void update(InputCommentDto input, List<Master> masters) {
        this.description = input.getDescription() == null ? this.description : input.getDescription();
        this.masters = input.getMasterId() == null ? this.masters : masters;
    }
}
