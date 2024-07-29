package knu.kproject.dto.UserDto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;


@Data
public class UserDto {
    private Long id;
    private String name;

    private String socialId;
    private boolean marketingEmailOptIn;
    private String contact;
    private String location;
    private String mbti;
    private String imageUrl;
    private List<ToolDto> tools = new ArrayList<>();

}
