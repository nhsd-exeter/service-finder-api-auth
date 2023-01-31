package uk.nhs.digital.uec.api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * The login attempts of a {@link User}.
 */
@Entity
@Table(name = "login_attempt")
@NoArgsConstructor
@Setter
@Getter
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private int attempts;

    public LoginAttempt(String emailAddress, int attempts) {
        this.emailAddress = emailAddress;
        this.attempts = attempts;
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        created = now;
        updated = now;
    }

    @PreUpdate
    private void preUpdate() {
        updated = LocalDateTime.now();
    }

}
