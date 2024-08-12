package knu.kproject.dto.board;

import knu.kproject.dto.comment.CommentDto;
import knu.kproject.entity.board.Board;
import knu.kproject.global.CATEGORY;
import knu.kproject.global.PROGRESS;
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
    private List<CommentDto> comments;

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
                .comments(board.getComments().stream()
                        .map(CommentDto::fromEntity)
                        .toList())
                .build();

        return dto;
    }
}