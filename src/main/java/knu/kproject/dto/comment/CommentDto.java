package knu.kproject.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.dto.board.MasterDto;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.comment.Comment;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private UUID id;
    private String description;
    private Timestamp createdAt;
    private String user;
    private List<MasterDto> masters;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = CommentDto.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .user(comment.getUser().getName())
                .masters(comment.getMasters().stream()
                        .map(MasterDto::fromEntity)
                        .toList())
                .build();

        return dto;
    }

}
