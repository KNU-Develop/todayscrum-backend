package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name="name_id")
    private String nameId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
