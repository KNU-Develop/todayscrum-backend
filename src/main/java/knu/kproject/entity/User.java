package knu.kproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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

    @Column(name="social_authentication")
    private String status;
    private String oauth2Id;
    private String role;

    public User(String name, String socialId, boolean requiredTermsAgree, boolean marketingEmailOptIn, String status) {
        this.name = name;
        this.socialId = socialId;
        this.requiredTermsAgree = requiredTermsAgree;
        this.marketingEmailOptIn = marketingEmailOptIn;
        this.status = status;
    }
}
