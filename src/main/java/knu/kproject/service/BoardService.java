package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.dto.board.InputBoardDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.notice.Notice;
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
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.awt.image.PackedColorModel;
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
    private final NoticeService noticeService;

    public UUID createBoard(Long token, UUID projectId, InputBoardDto boardDto) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User my = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser self = projectUserRepository.findByUserAndProject(my, project);

        Access.accessPossible(self, ROLE.WRITER);

        Board board = Board.builder()
                .title(boardDto.getTitle())
                .project(projectRepositroy.findById(projectId).orElseThrow())
                .user(my)
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .progress(boardDto.getProgress())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        boardRepository.save(board);

        if (boardDto.getMastersId() != null) {
            List<User> userList = projectUserRepository.findByProjectId(board.getProject().getId()).stream()
                    .map(projectUser -> projectUser.getUser())
                    .toList();
            List<Master> masters = new ArrayList<>();
            for (Long id : boardDto.getMastersId()) {
                userRepository.findById(id)
                        .filter(user -> userList.contains(user))
                        .ifPresent(user -> {
                            ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

                            if (Access.accessBoard2(projectUser, board)) {
                                Master master = Master.builder()
                                        .board(board)
                                        .user(user)
                                        .build();

                                masters.add(master);
                                if (!my.equals(user)) {
                                    NoticeDto noticeDto = NoticeDto.builder()
                                            .isRead(false)
                                            .title(my.getName() + "님이 " + user.getName() + "님을 멘션으로 호출했습니다.")
                                            .type(NOTICETYPE.멘션)
                                            .originId(board.getId())
                                            .originTable("board")
                                            .user(user)
                                            .build();

                                    noticeService.addNotice(user, noticeDto);
                                }
                            }
                        });
            }

            masterRepository.saveAll(masters);
            board.setMaster(masters);
            boardRepository.save(board);
        }

        return board.getId();
    }

    public List<BoardDto> findByAllBoard(Long token, UUID projectId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        Access.accessPossible(projectUser, ROLE.GUEST);

        List<Board> boards = boardRepository.findByProject(project);
        return boards.stream().map(BoardDto::fromEntity).toList();
    }

    public BoardDto findByBoard(Long token, UUID boardId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_BOARD));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(projectUser, ROLE.GUEST);

        return BoardDto.fromEntity(board);
    }

    @Transactional
    public void updateBoard(Long token, UUID boardId, InputBoardDto input) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User self = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_BOARD));
        ProjectUser my = projectUserRepository.findByUserAndProject(self, board.getProject());

        Access.accessPossible(my, ROLE.WRITER);
        Access.accessBoard(my, board);

        board.setTitle(input.getTitle() == null ? board.getTitle() : input.getTitle());
        board.setContent(input.getContent() == null ? board.getContent() : input.getContent());
        board.setCategory(input.getCategory() == null ? board.getCategory() : input.getCategory());
        board.setProgress(input.getProgress() == null ? board.getProgress() : input.getProgress());


        if (input.getMastersId() != null) {
            List<Master> masterList = board.getMaster();

            masterRepository.deleteAll(masterList);
            masterList.clear();

            List<User> projectUsers = projectUserRepository.findByProjectId(board.getProject().getId())
                    .stream().map(projectUser -> userRepository.findById(projectUser.getUser().getId()).orElse(null))
                    .toList();
            for (Long id : input.getMastersId()) {
                userRepository.findById(id)
                        .filter(user -> projectUsers.contains(user))
                        .ifPresent(user -> {
                            ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, board.getProject());

                            if (Access.accessBoard2(projectUser, board)) {
                                Master master = Master.builder()
                                        .board(board)
                                        .user(user)
                                        .build();

                                masterList.add(master);
                                if (!my.getUser().equals(user)) {
                                    NoticeDto noticeDto = NoticeDto.builder()
                                            .isRead(false)
                                            .title(my.getUser().getName() + "님이 " + user.getName() + "님을 멘션으로 호출했습니다.")
                                            .type(NOTICETYPE.멘션)
                                            .originId(board.getId())
                                            .originTable("board")
                                            .user(user)
                                            .build();

                                    noticeService.addNotice(user, noticeDto);
                                }
                            }
                        });
            }
            masterRepository.saveAll(masterList);
            board.setMaster(masterList);
        }
        boardRepository.save(board);
    }

    public void deleteBoard(Long token, UUID boardId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_BOARD));
        ProjectUser myRole = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(myRole, ROLE.WRITER);
        Access.accessBoard(myRole, board);

        List<Master> masterList = masterRepository.findByBoard(board);
        masterRepository.deleteAll(masterList);

        boardRepository.delete(board);
    }
}
