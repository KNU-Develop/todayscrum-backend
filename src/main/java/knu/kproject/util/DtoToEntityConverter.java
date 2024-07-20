package knu.kproject.util;

import knu.kproject.dto.ToolDto;
import knu.kproject.entity.Tool;

public class DtoToEntityConverter {
    public static Tool convertToTool(ToolDto toolDto) {
        Tool tool = new Tool();
        tool.setFigma(toolDto.getFigma());
        tool.setNotion(toolDto.getNotion());
        tool.setGithub(toolDto.getGithub());
        return tool;
    }
}
