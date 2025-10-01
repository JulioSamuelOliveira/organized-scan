package br.com.fiap.organized_scan.user;

import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usermottu")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    public User(OAuth2User principal, String email) {
        this.name = String.valueOf(principal.getAttributes().get("name"));
        this.email = email;
    }
}
