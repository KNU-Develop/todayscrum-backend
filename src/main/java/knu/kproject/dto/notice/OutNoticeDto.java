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
public class OutNoticeDto {
    private UUID id;
    private boolean isRead;
    private String title;
    private NOTICETYPE type;
    private String originTable;
    private UUID originId;
    private CHOICE choice;

    public static OutNoticeDto fromEntity(Notice notice) {
        OutNoticeDto noticeDto = OutNoticeDto.builder()
                .id(notice.getId())
                .isRead(notice.isRead())
                .title(notice.getTitle())
                .type(notice.getType())
                .originId(notice.getOriginId())
                .originTable(notice.getOriginTable())
                .choice(notice.getChoice())
                .build();
        return noticeDto;
    }
}
