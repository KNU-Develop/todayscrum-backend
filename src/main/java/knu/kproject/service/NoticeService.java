package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.notice.InNoticeDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.dto.notice.OutNoticeDto;
import knu.kproject.entity.notice.Notice;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.repository.NoticeRepositroy;
import knu.kproject.repository.ProjectRepositroy;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .build();

        noticeRepositroy.save(notice);
        user.getNotices().add(notice);
        userRepository.save(user);
    }

    public List<OutNoticeDto> getNotice(Long token) {
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);

        List<OutNoticeDto> noticeDtoList = user.getNotices().stream()
                .map(OutNoticeDto::fromEntity)
                .toList();

        return noticeDtoList;
    }

    public void acceptInvite(Long token, InNoticeDto input) {
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Notice notice = noticeRepositroy.findById(input.getNoticeId()).orElseThrow(EntityNotFoundException::new);
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, projectRepositroy.findById(notice.getOriginId()).orElseThrow(EntityNotFoundException::new));

        if (input.getChoice().equals(CHOICE.수락)) {
            projectUser.setChoice(CHOICE.수락);
            notice.setChoice(CHOICE.수락);

            projectUserRepository.save(projectUser);
        } else if (input.getChoice().equals(CHOICE.거절)) {
            projectUser.setChoice(CHOICE.거절);
            notice.setChoice(CHOICE.거절);
        }
        noticeRepositroy.save(notice);
    }
}
