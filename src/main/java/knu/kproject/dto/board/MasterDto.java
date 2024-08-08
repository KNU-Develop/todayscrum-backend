package knu.kproject.dto.board;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MasterDto {
    private String name;
    private String email;
}
