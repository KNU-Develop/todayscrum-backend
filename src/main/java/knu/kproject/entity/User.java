package knu.kproject.entity;

import jakarta.persistence.*;
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
    @Column(nullable = true)
    private boolean requiredTermsAgree;
    @Column(nullable = true)
    private boolean marketingEmailOptIn;

    private String phone;
    private String address;
    private String mbti;
    private String profileImage;

    @Column(name="social_authentication")
    private String status;
    private String oauth2Id;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tool> tools;

    public User(String name, String socialId, boolean requiredTermsAgree, boolean marketingEmailOptIn, String status) {
        this.name = name;
        this.socialId = socialId;
        this.requiredTermsAgree = requiredTermsAgree;
        this.marketingEmailOptIn = marketingEmailOptIn;
        this.status = status;
    }
}
