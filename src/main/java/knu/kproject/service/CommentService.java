package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import knu.kproject.dto.comment.CommentDto;
import knu.kproject.dto.comment.InputCommentDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.NOTICETYPE;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(self, ROLE.GUEST);

        List<CommentDto> commentDtos = board.getComments().stream()
                .map(CommentDto::fromEntity)
                .toList();
        return commentDtos;
    }

    public UUID addComment(Long token, UUID boardId, InputCommentDto input) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
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
                            Master master = Master.builder()
                                    .comment(comment)
                                    .user(user)
                                    .build();

                            masters.add(master);
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
                .title(user.getName() + "님이 " + user.getName() + "님이 댓글을 달았습니다.")
                .type(NOTICETYPE.댓글)
                .originId(comment.getId())
                .originTable("comment")
                .user(user)
                .build();

        noticeService.addNotice(user, noticeDto);

        return comment.getId();
    }

    @Transactional
    public void fixComment(Long token, UUID commentId, InputCommentDto input) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        Access.accessPossible(self, ROLE.WRITER);

        if (self == null || self.getRole().equals(ROLE.GUEST) || (!self.getUser().getName().equals(comment.getUser().getName()) && self.getRole().equals(ROLE.WRITER))) {
            throw new NullPointerException();
        }
        comment.setDescription(input.getDescription() == null ? comment.getDescription() : input.getDescription());

        if (input.getMasterId() != null && !input.getMasterId().isEmpty()) {
            List<Master> masterList = comment.getMasters();
            comment.getMasters().clear();
            masterRepository.deleteAll(masterList);

            comment.setMasters(input.getMasterId().stream()
                    .map(id -> Master.builder()
                            .user(userRepository.findById(id).orElseThrow(EntityNotFoundException::new))
                            .comment(comment)
                            .build())
                    .toList());
        }

        commentRepository.save(comment);
    }

    public void deleteComment(Long token, UUID commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        Access.accessPossible(self, ROLE.WRITER);

        if (self == null || self.getRole().equals(ROLE.GUEST) || (!self.getUser().getName().equals(comment.getUser().getName()) && self.getRole().equals(ROLE.WRITER))) {
            throw new NullPointerException();
        }

        commentRepository.delete(comment);

        commentRepository.save(comment);
    }
}
