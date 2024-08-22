package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import knu.kproject.dto.comment.CommentDto;
import knu.kproject.dto.comment.InputCommentDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final NoticeService noticeService;
    private final MasterRepository masterRepository;

    public List<CommentDto> getCommentList(Long token, UUID boardId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_BOARD));
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        ProjectUser self = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(self, ROLE.GUEST);

        List<CommentDto> commentDtos = board.getComments().stream()
                .map(CommentDto::fromEntity)
                .toList();
        return commentDtos;
    }

    public UUID addComment(Long token, UUID boardId, InputCommentDto input) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_BOARD));
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        ProjectUser self = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(self, ROLE.WRITER);

        Comment comment = Comment.builder()
                .description(input.getDescription())
                .board(board)
                .user(user)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();


        commentRepository.save(comment);

        if (!input.getMasterId().isEmpty()) {
            List<User> projectUsers = projectUserRepository.findByProjectId(board.getProject().getId())
                    .stream().map(projectUser -> userRepository.findById(projectUser.getUser().getId()).orElseThrow())
                    .toList();

            List<Master> masters = new ArrayList<>();
            for (Long id : input.getMasterId()) {
                userRepository.findById(id)
                        .filter(usr -> projectUsers.contains(user))
                        .ifPresent(usr -> {
                            ProjectUser projectUser = projectUserRepository.findByUserAndProject(usr, comment.getBoard().getProject());
                            if (Access.accessComment(projectUser, comment)) {
                                Master master = Master.builder()
                                        .comment(comment)
                                        .user(usr)
                                        .build();

                                masters.add(master);
                                if (!user.equals(usr)) {
                                    NoticeDto noticeDto = NoticeDto.builder()
                                            .isRead(false)
                                            .title(user.getName() + "님이 " + usr.getName() + "님을 멘션으로 호출했습니다.")
                                            .type(NOTICETYPE.멘션)
                                            .originId(comment.getId())
                                            .originTable("comment")
                                            .user(usr)
                                            .build();

                                    noticeService.addNotice(usr, noticeDto);
                                }
                            }
                        });
            }
            masterRepository.saveAll(masters);
            comment.setMasters(masters);
            commentRepository.save(comment);

        }
        board.getComments().add(comment);
        boardRepository.save(board);

        NoticeDto noticeDto = NoticeDto.builder()
                .isRead(false)
                .title(user.getName() + "님이 " + board.getUser().getName() + "님의 보드에 댓글을 달았습니다.")
                .type(NOTICETYPE.댓글)
                .originId(comment.getId())
                .originTable("comment")
                .user(board.getUser())
                .build();

        noticeService.addNotice(board.getUser(), noticeDto);

        return comment.getId();
    }

    @Transactional
    public void fixComment(Long token, UUID commentId, InputCommentDto input) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_COMMENT));
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        Access.accessPossible(self, ROLE.WRITER);

        if (self == null || self.getRole().equals(ROLE.GUEST) || (!self.getUser().getName().equals(comment.getUser().getName()) && self.getRole().equals(ROLE.WRITER))) {
            throw new ProjectException(ProjectErrorCode.FORBIDEN_ROLE);
        }
        comment.setDescription(input.getDescription() == null ? comment.getDescription() : input.getDescription());

        if (input.getMasterId() != null && !input.getMasterId().isEmpty()) {
            List<Master> masterList = comment.getMasters();
            comment.getMasters().clear();
            masterRepository.deleteAll(masterList);

            List<User> projectUsers = projectUserRepository.findByProjectId(comment.getBoard().getProject().getId())
                    .stream().map(projectUser -> userRepository.findById(projectUser.getUser().getId()).orElseThrow())
                    .toList();

            masterList.clear();

            for (Long id : input.getMasterId()) {
                userRepository.findById(id)
                        .filter(usr -> projectUsers.contains(user))
                        .ifPresent(usr -> {
                            ProjectUser projectUser = projectUserRepository.findByUserAndProject(usr, comment.getBoard().getProject());
                            if (Access.accessComment(projectUser, comment)) {
                                Master master = Master.builder()
                                        .comment(comment)
                                        .user(usr)
                                        .build();

                                masterList.add(master);
                                if (!user.equals(usr)) {
                                    NoticeDto noticeDto = NoticeDto.builder()
                                            .isRead(false)
                                            .title(user.getName() + "님이 " + usr.getName() + "님을 멘션으로 호출했습니다.")
                                            .type(NOTICETYPE.멘션)
                                            .originId(comment.getId())
                                            .originTable("comment")
                                            .user(usr)
                                            .build();

                                    noticeService.addNotice(usr, noticeDto);
                                }
                            }

                        });

                masterRepository.saveAll(masterList);
                comment.setMasters(masterList);
                commentRepository.save(comment);

            }
        }
        commentRepository.save(comment);
    }

    public void deleteComment(Long token, UUID commentId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_COMMENT));
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        Access.accessPossible(self, ROLE.WRITER);
        Access.accessComment2(self, comment);

        commentRepository.delete(comment);

        commentRepository.save(comment);
    }
}
