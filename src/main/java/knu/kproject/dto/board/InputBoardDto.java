package knu.kproject.dto.board;

import knu.kproject.entity.CATEGORY;
import knu.kproject.entity.Master;
import knu.kproject.entity.PROGRESS;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InputBoardDto {
    private String title;
    private String content;
    private CATEGORY category;
    private PROGRESS progress;
    private List<Long> masters;
}
