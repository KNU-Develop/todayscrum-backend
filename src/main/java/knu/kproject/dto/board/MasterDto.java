package knu.kproject.dto.board;

import knu.kproject.entity.board.Master;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MasterDto {
    private Long id;
    private String name;
    private String email;

    public static MasterDto fromEntity(Master master) {
        return MasterDto.builder()
                .id(master.getUser().getId())
                .name(master.getUser().getName())
                .email(master.getUser().getEmail())
                .build();
    }
}
