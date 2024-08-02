package knu.kproject.dto.UserDto;


import knu.kproject.entity.Tool;
import knu.kproject.entity.UserTool;
import lombok.Data;

@Data
public class ToolDto {
    private Long id;
    private String name;
    private  String email;

    public static ToolDto fromEntity(UserTool userTool) {
        ToolDto dto = new ToolDto();
        Tool tool = userTool.getTool();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setEmail(userTool.getEmail());
        return dto;
    }
}
