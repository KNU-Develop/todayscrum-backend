package knu.kproject.dto.notice;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.entity.notice.Notice;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeDto {
    private UUID id;
    private boolean isRead;
    private String title;
    private NOTICETYPE type;
    private String originTable;
    private UUID originId;
    private User user;
    private CHOICE choice;

    public static NoticeDto fromEntity(Notice notice) {
        NoticeDto noticeDto = NoticeDto.builder()
                .id(notice.getId())
                .isRead(notice.isRead())
                .title(notice.getTitle())
                .type(notice.getType())
                .originId(notice.getOriginId())
                .originTable(notice.getOriginTable())
                .user(notice.getUser())
                .choice(notice.getChoice())
                .build();
        return noticeDto;
    }
}
