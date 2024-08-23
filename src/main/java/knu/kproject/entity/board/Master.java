package knu.kproject.entity.board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.user.User;
import lombok.*;
import org.hibernate.annotations.ManyToAny;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MASTER")
public class Master {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private Board board;


    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private Comment comment;

    public Master(User user, Board board) {
        this.user = user;
        this.board = board;
    }

    public Master(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
