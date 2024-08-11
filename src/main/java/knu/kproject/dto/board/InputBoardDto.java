package knu.kproject.dto.board;

import knu.kproject.global.CATEGORY;
import knu.kproject.global.PROGRESS;
import lombok.Data;

import java.util.List;

@Data
public class InputBoardDto {
    private String title;
    private String content;
    private CATEGORY category;
    private PROGRESS progress;
    private List<Long> masters;
}
