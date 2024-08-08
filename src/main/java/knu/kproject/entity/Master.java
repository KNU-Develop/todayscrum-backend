package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MASTER")
public class Master {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "board",nullable = false)
    private Board boardId;

    @Column(nullable = false)
    private Long userId;
}
