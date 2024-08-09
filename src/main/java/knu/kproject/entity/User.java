package knu.kproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.MBTI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(nullable = false, unique = false, name = "social_id")
    private String socialId;
    @Email
    private String email;
    @Column(nullable = true)
    private boolean requiredTermsAgree;
    @Column(nullable = true)
    private boolean marketingEmailOptIn;

    private String contact;
    private String location;

    @Enumerated(EnumType.STRING)
    private MBTI mbti;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="social_authentication")
    private UserStatus status;
    private String oauth2Id;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserStack> userStacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserTool> userTools;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<UserSchedule> userSchedules = new ArrayList<>();

    public User(String name, String socialId, UserStatus status) {
        this.name = name;
        this.socialId = socialId;
        this.status = status;
    }
    public void updateUserInfo(AdditionalUserInfo userDto) {
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            this.setName(userDto.getName());
        }
        this.setContact(userDto.getContact());
        this.setEmail(userDto.getEmail());
        this.setMarketingEmailOptIn(userDto.isMarketingEmailOptIn());

        this.setMbti(userDto.getMbti());
        this.setLocation(userDto.getLocation());
        this.setImageUrl(userDto.getImageUrl());
    }
    public void joinInfo(AdditionalUserInfo userInfo) {
        if (userInfo.getName() != null && !userInfo.getName().isEmpty()) {
            this.setName(userInfo.getName());
        }
        this.setContact(userInfo.getContact());
        this.setEmail(userInfo.getEmail());
        this.setStatus(UserStatus.JOIN);
        this.setMarketingEmailOptIn(userInfo.isMarketingEmailOptIn());
        this.setRequiredTermsAgree(userInfo.isRequiredTermsAgree());
    }

    public void withDraw(UserDto userDto){
        this.setStatus(UserStatus.WITHDRAW);
    }


    /*
        User <-> Schedule 연관 관계 메서드
     */
    public void addUserSchedule(UserSchedule userSchedule) {
        this.userSchedules.add(userSchedule);
        userSchedule.setUser(this);
    }

    public void removeUserSchedule(UserSchedule userSchedule) {
        this.userSchedules.remove(userSchedule);
        userSchedule.setUser(null);
    }
}
