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
        if (!projectRepositroy.existsById(projectId)){
            throw new EntityNotFoundException();
        }
        ProjectUser self = projectUserRepository.findByUserIdAndProjectId(token, projectId);
        if (self == null || self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        Board board = Board.builder()
                .title(boardDto.getTitle())
                .project(projectRepositroy.findById(projectId).orElseThrow())
                .userId(self.getUserId())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .progress(boardDto.getProgress())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        boardRepository.save(board);

        if (boardDto.getMasters() != null) {
            List<Master> masters = new ArrayList<>();
            for (String email : boardDto.getMasters()) {
                User user = userRepository.findByEmail(email);
                if (user==null) continue;
                Master master = Master.builder()
                        .boardId(board)
                        .userId(user.getId())
                        .build();
                masters.add(master);
            }

            masterRepository.saveAll(masters);
            board.setMaster(masters);
            boardRepository.save(board);
        }

        return board.getId();
    }

    public List<MasterDto> convertToMaster(List<Master> masters) {
        List<MasterDto> masterDto = new ArrayList<>();
        for (Master master : masters) {
            User user = userRepository.findById(master.getUserId()).orElseThrow();
            MasterDto dto = MasterDto.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
            masterDto.add(dto);
        }
        return masterDto;
    }
    public BoardDto formDto(Board board) {
        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .progress(board.getProgress())
                .createdAt(board.getCreatedAt())
                .masters(convertToMaster(board.getMaster()))
                .build();

        return boardDto;
    }

    public List<BoardDto> findByAllBoard(Long token, UUID projectId) {
        if (!projectRepositroy.existsById(projectId)) throw new IllegalArgumentException();
        if (!projectUserRepository.existsByProjectIdAndUserId(projectId, token)) throw new NullPointerException();

        List<Board> boards = boardRepository.findByProject(projectRepositroy.findById(projectId).orElseThrow());
        return boards.stream().map(this::formDto).toList();
    }

    public BoardDto findByBoard(Long token, UUID boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        ROLE myRole = projectUserRepository.findByUserIdAndProjectId(token, board.getProject().getId()).getRole();
        if (myRole == null) {
            throw new NullPointerException();
        }
        return formDto(board);
    }

    public void updateBoard(Long token, UUID boardId, InputBoardDto input) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        ROLE myRole = projectUserRepository.findByUserIdAndProjectId(token, board.getProject().getId()).getRole();
        System.out.println(myRole);
        if (myRole == null || myRole.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        board.setTitle(input.getTitle()==null ? board.getTitle() : input.getTitle());
        board.setContent(input.getContent() == null ? board.getContent() : input.getContent());
        board.setCategory(input.getCategory());
        board.setProgress(input.getProgress());

        List<Master> masterList = masterRepository.findByBoardId(board);
        masterRepository.deleteAll(masterList);

        masterList = new ArrayList<>();
        List<User> projectUsers = projectUserRepository.findByProjectId(board.getProject().getId())
                .stream().map(projectUser -> userRepository.findById(projectUser.getUserId()).orElseThrow())
                .toList();
        if (input.getMasters() != null) {
            for (String email : input.getMasters()) {
                User user = userRepository.findByEmail(email);
                if (!projectUsers.contains(user)) continue;
                if (user == null) continue;
                Master master = Master.builder()
                        .boardId(board)
                        .userId(user.getId())
                        .build();
                masterList.add(master);
            }
        }
        masterRepository.saveAll(masterList);
        board.setMaster(masterList);
        boardRepository.save(board);
    }
    public void deleteBoard(Long token, UUID boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        ROLE myRole = projectUserRepository.findByUserIdAndProjectId(token, board.getProject().getId()).getRole();

        if (myRole == null || myRole.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        List<Master> masterList = masterRepository.findByBoardId(board);
        masterRepository.deleteAll(masterList);

        boardRepository.delete(board);
    }
}
