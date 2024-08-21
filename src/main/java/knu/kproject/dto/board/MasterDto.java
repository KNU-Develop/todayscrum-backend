package knu.kproject.dto.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.comment.Comment;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterDto {
    private Long id;
    private String name;
    private String logo;
    private String email;

    public static MasterDto fromEntity(Master master) {
        return MasterDto.builder()
                .id(master.getUser().getId())
                .name(master.getUser().getName())
                .logo(master.getUser().getImageUrl())
                .email(master.getUser().getEmail())
                .build();
    }

    public static MasterDto fromEntity(Comment comment) {
        return MasterDto.builder()
                .id(comment.getUser().getId())
                .name(comment.getUser().getName())
                .logo(comment.getUser().getImageUrl())
                .email(comment.getUser().getEmail())
                .build();
    }
}
