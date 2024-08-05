package knu.kproject.dto.UserDto;


import knu.kproject.entity.ToolName;
import knu.kproject.entity.UserTool;
import lombok.Data;

@Data
public class ToolInfoDto {
    private ToolName toolName;
    private  String email;

    public static ToolInfoDto fromEntity(UserTool userTool) {
        ToolInfoDto dto = new ToolInfoDto();
        dto.setToolName(userTool.getTool());
        dto.setEmail(userTool.getEmail());
        return dto;
    }
}
