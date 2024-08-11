package knu.kproject.entity.board;

import jakarta.persistence.*;
import knu.kproject.entity.user.User;
import lombok.*;

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
    private Board board;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}
