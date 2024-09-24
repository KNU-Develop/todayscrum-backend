package knu.kproject.dto.notice;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.notice.Notice;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeDto {
    private UUID id;
    private boolean isread;
    private String title;
    private NOTICETYPE type;
    private UUID originTable;
    private UUID originId;
    private User user;
    private CHOICE choice;
    private Timestamp createdAt;

    public static NoticeDto fromEntity(Notice notice) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setId(notice.getId());
        noticeDto.setIsread(notice.isRead());
        noticeDto.setTitle(notice.getTitle());
        noticeDto.setType(notice.getType());
        noticeDto.setOriginId(notice.getOriginId());
        noticeDto.setOriginTable(notice.getOriginTable());
        noticeDto.setUser(notice.getUser());
        noticeDto.setChoice(notice.getChoice());
        noticeDto.setCreatedAt(notice.getCreateAt());

        return noticeDto;
    }

    public NoticeDto(User inviter, User invited, Project project) {
        this.isread = false;
        this.title = inviter.getName() + "님이 " + project.getTitle() + "에 초대했습니다.";
        this.type = NOTICETYPE.초대;
        this.originId = project.getId();
        this.originTable = null;
        this.user = invited;
        this.choice = CHOICE.전송;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public NoticeDto(User inviter, User invited, Board board, NOTICETYPE type) {
        this.isread = false;
        if (type.equals(NOTICETYPE.댓글)) {
            this.title = inviter.getName() + "님이 " + board.getTitle() + "에 댓글을 달았습니다..";
        } else if (type.equals(NOTICETYPE.멘션)) {
            this.title = inviter.getName() + "님이 @" + invited.getName() + "을 호출했습니다.";
        }
        this.type = type;
        this.originId = board.getId();
        this.originTable = board.getProject().getId();
        this.user = invited;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public NoticeDto(User inviter, User invited, Comment comment) {
        this.isread = false;
        this.title = inviter.getName() + "님이 @" + invited.getName() + "을 호출했습니다.";
        this.type = NOTICETYPE.멘션;
        this.originId = comment.getId();
        this.originTable = comment.getBoard().getProject().getId();
        this.user = invited;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}
