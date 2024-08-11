package knu.kproject.dto.UserDto;


import knu.kproject.entity.user.UserStack;
import lombok.Data;

@Data
public class UserStackDto {
    private Long id;
    private Long userId;
    private Long stackId;
    private String stackName;

    public static UserStackDto fromEntity(UserStack userStack) {
        UserStackDto dto = new UserStackDto();
        dto.setId(userStack.getId());
        dto.setUserId(userStack.getUser().getId());
        dto.setStackId(userStack.getStack().getId());
        dto.setStackName(userStack.getStack().getName());
        return dto;
    }
}
