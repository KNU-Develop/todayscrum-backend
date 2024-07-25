package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name="name_id")
    private String nameId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Tool(Long id, String name, String nameId, User user) {
        this.id = id;
        this.name = name;
        this.nameId = nameId;
        this.user = user;
    }
}
