package knu.kproject.entity.notice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTICE")
public class Notice {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column
    private boolean isRead;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private NOTICETYPE type;

    @Column(nullable = false)
    private String originTable;

    @Column(nullable = false)
    private UUID originId;

    @JoinColumn(nullable = false)
    @ManyToOne
    @JsonBackReference
    private User user;

    @Column
    private CHOICE choice;
}
