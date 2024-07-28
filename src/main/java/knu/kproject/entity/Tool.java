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
    private String email;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Tool(String name, String email, User user) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user = user;
    }
    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
