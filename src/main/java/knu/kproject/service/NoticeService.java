package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.notice.InNoticeDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.dto.notice.OutNoticeDto;
import knu.kproject.entity.notice.Notice;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.CHOICE;
import knu.kproject.repository.NoticeRepositroy;
import knu.kproject.repository.ProjectRepositroy;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepositroy noticeRepositroy;
    private final UserRepository userRepository;
    private final ProjectRepositroy projectRepositroy;
    private final ProjectUserRepository projectUserRepository;

    public void addNotice(User user, NoticeDto input) {
        Notice notice = Notice.builder()
                .isRead(false)
                .title(input.getTitle())
                .type(input.getType())
                .originTable(input.getOriginTable())
                .originId(input.getOriginId())
                .user(input.getUser())
                .choice(input.getChoice())
                .createAt(new Timestamp(System.currentTimeMillis()))
                .build();

        noticeRepositroy.save(notice);
        user.getNotices().add(notice);
        userRepository.save(user);
    }

    public List<OutNoticeDto> getNotice(Long token) {
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));

        List<OutNoticeDto> noticeDtoList = user.getNotices().stream()
                .sorted(Comparator.comparing(Notice::getCreateAt).reversed())
                .map(OutNoticeDto::fromEntity)
                .toList();

        return noticeDtoList;
    }

    public void acceptInvite(Long token, InNoticeDto input) {
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Notice notice = noticeRepositroy.findById(input.getNoticeId()).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_NOTICE));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, projectRepositroy.findById(notice.getOriginId()).orElse(null));

        if (input.getChoice().equals(CHOICE.수락)) {
            projectUser.setChoice(CHOICE.수락);
            notice.setChoice(CHOICE.수락);

            projectUserRepository.save(projectUser);
        } else if (input.getChoice().equals(CHOICE.거절)) {
            projectUser.setChoice(CHOICE.거절);
            notice.setChoice(CHOICE.거절);
        } else if (input.getChoice().equals(CHOICE.전송)) {
            projectUser.setChoice(CHOICE.전송);
            notice.setChoice(CHOICE.전송);
        }
        noticeRepositroy.save(notice);
    }
}
