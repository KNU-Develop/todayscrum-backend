package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
//    @Column(nullable = false, unique = true, name="social_email")
//    private String email;
    @Column(nullable = false, unique = false, name = "social_id")
    private String socialId;
    @Column(nullable = false)
    boolean requiredTermsAgree;
    @Column(nullable = false)
    boolean marketingEmailOptIn;

    private String phone;
    private String address;
    private String mbti;

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
