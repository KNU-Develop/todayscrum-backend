package knu.kproject.dto.comment;

import knu.kproject.entity.comment.Comment;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class CommentDto {
    private UUID id;
    private String description;
    private Timestamp createdAt;
    private String user;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = CommentDto.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .user(comment.getUser().getName())
                .build();
        return dto;
    }

}
