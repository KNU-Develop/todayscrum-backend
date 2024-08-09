package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.dto.board.InputBoardDto;
import knu.kproject.dto.board.MasterDto;
import knu.kproject.entity.*;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final ProjectRepositroy projectRepositroy;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final BoardRepository boardRepository;
    private final MasterRepository masterRepository;

    public UUID createBoard(Long token, UUID projectId ,InputBoardDto boardDto) {
        User my  = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(projectId).orElseThrow(EntityNotFoundException::new);

        ProjectUser self = projectUserRepository.findByUserAndProject(my, project);
        if (self == null || self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        Board board = Board.builder()
                .title(boardDto.getTitle())
                .project(projectRepositroy.findById(projectId).orElseThrow())
                .userId(self.getUser().getId())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .progress(boardDto.getProgress())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        boardRepository.save(board);

        if (boardDto.getMasters() != null) {
            List<Master> masters = new ArrayList<>();
            for (Long id : boardDto.getMasters()) {
                userRepository.findById(id).ifPresent(user -> {
                    Master master = Master.builder()
                            .board(board)
                            .user(user)
                            .build();
                    masters.add(master);
                });
            }

            masterRepository.saveAll(masters);
            board.setMaster(masters);
            boardRepository.save(board);
        }

        return board.getId();
    }
    public List<BoardDto> findByAllBoard(Long token, UUID projectId) {
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(projectId).orElseThrow(EntityNotFoundException::new);
        if (!projectUserRepository.existsByProjectAndUser(project, user)) throw new NullPointerException();

        List<Board> boards = boardRepository.findByProject(projectRepositroy.findById(projectId).orElseThrow());
        return boards.stream().map(BoardDto::fromEntity).toList();
    }

    public BoardDto findByBoard(Long token, UUID boardId) {
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(board.getProject().getId()).orElseThrow(EntityNotFoundException::new);
        ROLE myRole = projectUserRepository.findByUserAndProject(user, project).getRole();
        if (myRole == null) {
            throw new NullPointerException();
        }
        return BoardDto.fromEntity(board);
    }

    public void updateBoard(Long token, UUID boardId, InputBoardDto input) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        User self = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(board.getProject().getId()).orElseThrow(EntityNotFoundException::new);

        ROLE myRole = projectUserRepository.findByUserAndProject(self, project).getRole();
        System.out.println(myRole);
        if (myRole == null || myRole.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        board.setTitle(input.getTitle()==null ? board.getTitle() : input.getTitle());
        board.setContent(input.getContent() == null ? board.getContent() : input.getContent());
        board.setCategory(input.getCategory() == null ? board.getCategory() : input.getCategory());
        board.setProgress(input.getProgress() == null ? board.getProgress() : input.getProgress());

        List<Master> masterList = masterRepository.findByBoard(board);
        masterRepository.deleteAll(masterList);

        masterList = new ArrayList<>();
        List<User> projectUsers = projectUserRepository.findByProjectId(board.getProject().getId())
                .stream().map(projectUser -> userRepository.findById(projectUser.getUser().getId()).orElseThrow())
                .toList();
        if (input.getMasters() != null) {
            for (Long id : input.getMasters()) {
                Optional<User> user = userRepository.findById(id);
                if (!projectUsers.contains(user)) continue;
                if (user.isPresent()) {
                    Master master = Master.builder()
                            .board(board)
                            .user(user.get())
                            .build();
                    masterList.add(master);
                }
            }
        }
        masterRepository.saveAll(masterList);
        board.setMaster(masterList);
        boardRepository.save(board);
    }
    public void deleteBoard(Long token, UUID boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = board.getProject();
        ROLE myRole = projectUserRepository.findByUserAndProject(user, project).getRole();

        if (myRole == null || myRole.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        List<Master> masterList = masterRepository.findByBoard(board);
        masterRepository.deleteAll(masterList);

        boardRepository.delete(board);
    }
}
