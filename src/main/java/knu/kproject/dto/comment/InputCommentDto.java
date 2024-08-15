package knu.kproject.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputCommentDto {
    private String title;
    private String description;
}
