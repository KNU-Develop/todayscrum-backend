package knu.kproject.dto.UserDto;

import knu.kproject.entity.User;

public class UserHeadDto {
    private Long id;
    private String name;
    private String email;

    public UserHeadDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
