package knu.kproject.dto.project;

import lombok.Data;

import java.util.List;

@Data
public class InviteDto {
    private Long projectId;
    private List<String> userName;
}
