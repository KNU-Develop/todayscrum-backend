package knu.kproject.dto.board;

import knu.kproject.entity.Board;
import knu.kproject.entity.CATEGORY;
import knu.kproject.entity.Master;
import knu.kproject.entity.PROGRESS;
import knu.kproject.service.BoardService;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BoardDto {
    private UUID id;
    private String title;
    private String content;
    private CATEGORY category;
    private PROGRESS progress;
    private Timestamp createdAt;
    private List<MasterDto> masters;

    public static BoardDto fromEntity(Board board) {
        BoardDto dto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .progress(board.getProgress())
                .createdAt(board.getCreatedAt())
                .masters(board.getMaster().stream()
                        .map(MasterDto::fromEntity)
                        .toList())
                .build();
        return dto;
    }
}