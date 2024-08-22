package knu.kproject.dto.UserDto;

import lombok.Data;

@Data
public class JoinUserDto {
    private String name;
    private String contact;
    private boolean requiredTermsAgree;
    private boolean marketingEmailOptIn;

}
