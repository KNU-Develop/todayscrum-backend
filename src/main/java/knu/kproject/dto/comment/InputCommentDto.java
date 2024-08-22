package knu.kproject.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class InputCommentDto {
    private String description;
    private List<Long> masterId;
}
