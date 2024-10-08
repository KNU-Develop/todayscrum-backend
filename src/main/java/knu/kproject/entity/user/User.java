package knu.kproject.entity.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.JoinUserDto;
import knu.kproject.dto.UserDto.UpdateUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.board.Board;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.board.Master;
import knu.kproject.entity.notice.Notice;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.global.MBTI;
import knu.kproject.global.ROLE;
import knu.kproject.global.UserStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "social_authentication")
    private UserStatus status;
    private String oauth2Id;
    private ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserStack> userStacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserTool> userTools;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<UserSchedule> userSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Board> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Master> masters;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Notice> notices;

    @Column
    private String color;

    public User(String name, String socialId, String email, UserStatus status) {
        this.name = name.trim();
        this.socialId = socialId.trim();
        this.email = email.trim();
        this.status = status;
    }

    public void updateUserInfo(UpdateUserDto userInfo) {
        if (userInfo.getName() != null && !userInfo.getName().trim().isEmpty()) {
            this.name = userInfo.getName().trim();
        }
        if (userInfo.getContact() != null && !userInfo.getContact().trim().isEmpty()) {
            this.contact = userInfo.getContact();
        }
        this.marketingEmailOptIn = userInfo.isMarketingEmailOptIn();
        this.mbti = userInfo.getMbti();
        this.location = userInfo.getLocation().trim();
        this.imageUrl = userInfo.getImageUrl();
    }

    public void joinInfo(JoinUserDto userInfo) {
        if (userInfo.getName() == null || userInfo.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (userInfo.getContact() == null || userInfo.getContact().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact cannot be empty.");
        }
        this.name = userInfo.getName().trim();
        this.contact = userInfo.getContact();
        this.status = UserStatus.JOIN;
        this.marketingEmailOptIn = userInfo.isMarketingEmailOptIn();
        this.requiredTermsAgree = userInfo.isRequiredTermsAgree();
    }

    public void addAdditionalInfo(AdditionalUserInfo userInfo) {
        this.mbti = userInfo.getMbti();
        this.location = userInfo.getLocation().trim();
        this.imageUrl = userInfo.getImageUrl();
    }

    public void withDraw(UserDto userDto) {
        this.status = UserStatus.WITHDRAW;
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
    public void updateUserColor(UpdateUserDto updateUserDto){
        this.color = updateUserDto.getColor();
    }
}
