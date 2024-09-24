package knu.kproject.entity.notice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import knu.kproject.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Not;

import java.sql.Timestamp;
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
    private UUID originTable;

    @Column(nullable = false)
    private UUID originId;

    @Column(nullable = false, updatable = false)
    private Timestamp createAt;

    @JoinColumn(nullable = false)
    @ManyToOne
    @JsonBackReference
    private User user;

    @Column
    private CHOICE choice;

    public Notice(NoticeDto dto) {
        this.isRead = false;
        this.title = dto.getTitle();
        this.type = dto.getType();
        this.originId = dto.getOriginId();
        this.originTable = dto.getOriginTable();
        this.createAt = dto.getCreatedAt();
        this.choice = dto.getChoice();
        this.user = dto.getUser();
    }
}
