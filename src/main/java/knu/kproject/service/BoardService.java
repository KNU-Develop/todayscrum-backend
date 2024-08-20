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
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

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
    private final NoticeRepositroy noticeRepositroy;

    public UUID createBoard(Long token, UUID projectId, InputBoardDto boardDto) {
        User my = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(projectId).orElseThrow(EntityNotFoundException::new);

        ProjectUser self = projectUserRepository.findByUserAndProject(my, project);

        Access.accessPossible(self, ROLE.WRITER);

        Board board = Board.builder()
                .title(boardDto.getTitle())
                .project(projectRepositroy.findById(projectId).orElseThrow())
                .userName(my.getName())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .progress(boardDto.getProgress())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        boardRepository.save(board);

        if (boardDto.getMastersId() != null) {
            List<Master> masters = new ArrayList<>();
            for (Long id : boardDto.getMastersId()) {
                userRepository.findById(id).ifPresent(user -> {
                    Master master = Master.builder()
                            .board(board)
                            .user(user)
                            .build();
                    masters.add(master);

                    NoticeDto noticeDto = NoticeDto.builder()
                            .isRead(false)
                            .title(my.getName() + "님이 " + user.getName() + "님을 멘션으로 호출했습니다.")
                            .type(NOTICETYPE.멘션)
                            .originId(board.getId())
                            .originTable("board")
                            .user(user)
                            .build();

                    noticeService.addNotice(user, noticeDto);
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
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        Access.accessPossible(projectUser, ROLE.GUEST);

        return BoardDto.fromEntity(board);
    }

    @Transactional
    public void updateBoard(Long token, UUID boardId, InputBoardDto input) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        User self = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        Project project = projectRepositroy.findById(board.getProject().getId()).orElseThrow(EntityNotFoundException::new);

        ProjectUser my = projectUserRepository.findByUserAndProject(self, project);

        Access.accessPossible(my, ROLE.WRITER);

        board.setTitle(input.getTitle() == null ? board.getTitle() : input.getTitle());
        board.setContent(input.getContent() == null ? board.getContent() : input.getContent());
        board.setCategory(input.getCategory() == null ? board.getCategory() : input.getCategory());
        board.setProgress(input.getProgress() == null ? board.getProgress() : input.getProgress());


        if (input.getMastersId() != null) {
            List<Master> masterList = board.getMaster();

            masterRepository.deleteAll(masterList);
            masterList.clear();

            List<User> projectUsers = projectUserRepository.findByProjectId(board.getProject().getId())
                    .stream().map(projectUser -> userRepository.findById(projectUser.getUser().getId()).orElseThrow())
                    .toList();
            for (Long id : input.getMastersId()) {
                userRepository.findById(id)
                        .filter(user -> projectUsers.contains(user))
                        .ifPresent(user -> {
                            Master master = Master.builder()
                                    .board(board)
                                    .user(user)
                                    .build();

                            masterList.add(master);

                            NoticeDto noticeDto = NoticeDto.builder()
                                    .isRead(false)
                                    .title(self.getName() + "님이 " + user.getName() + "님을 멘션으로 호출했습니다.")
                                    .type(NOTICETYPE.멘션)
                                    .originId(board.getId())
                                    .originTable("board")
                                    .user(user)
                                    .build();

                            noticeService.addNotice(user, noticeDto);
                        });
            }
            masterRepository.saveAll(masterList);
            board.setMaster(masterList);
        }
        boardRepository.save(board);
    }

    public void deleteBoard(Long token, UUID boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(token).orElseThrow(EntityNotFoundException::new);
        ProjectUser myRole = projectUserRepository.findByUserAndProject(user, board.getProject());

        Access.accessPossible(myRole, ROLE.WRITER);

        if (myRole.getRole() == null || myRole.getRole().equals(ROLE.GUEST) || (!user.getName().equals(board.getUserName()) && myRole.getRole().equals(ROLE.WRITER))) {
            throw new NullPointerException();
        }

        List<Master> masterList = masterRepository.findByBoard(board);
        masterRepository.deleteAll(masterList);

        boardRepository.delete(board);
    }
}
