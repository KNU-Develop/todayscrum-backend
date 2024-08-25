package knu.kproject.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.global.CHOICE;
import knu.kproject.global.ROLE;
import lombok.Data;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectUserDto {
    private Long projectId;
    private String userId;
    private ROLE role;
    private String color;
    private CHOICE choice;
}
