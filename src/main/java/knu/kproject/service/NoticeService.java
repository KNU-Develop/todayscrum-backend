package knu.kproject.service;

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
import knu.kproject.repository.ProjectRepository;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepositroy noticeRepositroy;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;

    public void addNotice(User user, NoticeDto input) {
        Notice notice = new Notice(input);
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

    public void acceptInvite(Long token, UUID noticeId, InNoticeDto input) {
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Notice notice = noticeRepositroy.findById(noticeId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_NOTICE));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, projectRepository.findById(notice.getOriginId()).orElse(null));

        if (input.isRead()) {
            notice.setRead(true);
            noticeRepositroy.save(notice);
        }
        if (input.getChoice() != null) {
            if (input.getChoice().equals(CHOICE.수락)) {
                projectUser.setChoice(CHOICE.수락);
                notice.setChoice(CHOICE.수락);
            } else if (input.getChoice().equals(CHOICE.거절)) {
                projectUser.setChoice(CHOICE.거절);
                notice.setChoice(CHOICE.거절);
            }

            noticeRepositroy.delete(notice);
            projectUserRepository.save(projectUser);
        }
    }
}
