package knu.kproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = false, name = "social_id")
    private String socialId;
    @Column(unique = true)
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

    @Column(name="social_authentication")
    private String status;
    private String oauth2Id;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserStack> userStacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserTool> userTools;

    public User(String name, String socialId, boolean requiredTermsAgree, boolean marketingEmailOptIn, String status) {
        this.name = name;
        this.socialId = socialId;
        this.requiredTermsAgree = requiredTermsAgree;
        this.marketingEmailOptIn = marketingEmailOptIn;
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
        this.setStatus("JOIN");
        this.setMarketingEmailOptIn(userInfo.isMarketingEmailOptIn());
        this.setRequiredTermsAgree(userInfo.isRequiredTermsAgree());
    }

    public void withDraw(UserDto userDto){
        this.setStatus("WITHDRAW");
    }
}
