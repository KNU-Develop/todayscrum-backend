package knu.kproject.dto.board;

import knu.kproject.entity.Board;
import knu.kproject.entity.CATEGORY;
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
}