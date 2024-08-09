package knu.kproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER")
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
    private ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserStack> userStacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserTool> userTools;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Master> masters;

    public User(String name, String socialId, String email, UserStatus status) {
        this.name = name;
        this.socialId = socialId;
        this.email = email;
        this.status = status;
    }
    public void updateUserInfo(AdditionalUserInfo userInfo) {
        if (userInfo.getName() != null && !userInfo.getName().trim().isEmpty()) {
            this.setName(userInfo.getName());
        }
        if (userInfo.getEmail() != null && !userInfo.getEmail().trim().isEmpty()) {
            this.setEmail(userInfo.getEmail());
        }
        if (userInfo.getContact() != null && !userInfo.getContact().trim().isEmpty()) {
            this.setContact(userInfo.getContact());
        }
        this.setMarketingEmailOptIn(userInfo.isMarketingEmailOptIn());

        this.setMbti(userInfo.getMbti());
        this.setLocation(userInfo.getLocation());
        this.setImageUrl(userInfo.getImageUrl());
    }
    public void joinInfo(AdditionalUserInfo userInfo) {
        if (userInfo.getName() != null && !userInfo.getName().trim().isEmpty()) {
            this.setName(userInfo.getName());
        }
        if (userInfo.getEmail() != null && !userInfo.getEmail().trim().isEmpty()) {
            this.setEmail(userInfo.getEmail());
        }
        if (userInfo.getContact() != null && !userInfo.getContact().trim().isEmpty()) {
            this.setContact(userInfo.getContact());
        }
        this.setStatus(UserStatus.JOIN);
        this.setMarketingEmailOptIn(userInfo.isMarketingEmailOptIn());
        this.setRequiredTermsAgree(userInfo.isRequiredTermsAgree());
    }

    public void withDraw(UserDto userDto){
        this.setStatus(UserStatus.WITHDRAW);
    }
}
