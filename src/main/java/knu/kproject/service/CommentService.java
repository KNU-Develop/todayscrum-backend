package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.comment.CommentDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.ROLE;
import knu.kproject.repository.BoardRepository;
import knu.kproject.repository.CommentRepository;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
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

    public List<CommentDto> getCommentList(Long token, UUID boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, board.getProject());

        if (self == null) {
            throw new NullPointerException();
        }

        List<CommentDto> commentDtos = board.getComments().stream()
                .map(CommentDto::fromEntity)
                .toList();
        return commentDtos;
    }

    public void addComment(Long token, UUID boardId, CommentDto input) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, board.getProject());

        if (self == null || self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        Comment comment = Comment.builder()
                .title(input.getTitle())
                .description(input.getDescription())
                .board(board)
                .user(user)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        board.getComments().add(comment);
        commentRepository.save(comment);
        boardRepository.save(board);
    }

    public void fixComment(Long token, UUID commentId, CommentDto input) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        if (self == null || self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        comment.setTitle(input.getTitle());
        comment.setDescription(input.getDescription());

        commentRepository.save(comment);
    }

    public void deleteComment(Long token, UUID commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser self = projectUserRepository.findByUserAndProject(user, comment.getBoard().getProject());

        if (self == null || self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        commentRepository.delete(comment);

        commentRepository.save(comment);
    }
}
