package uk.nhs.digital.uec.api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents some notable event in the system, intended to be used for audit purposes.
 */
@Entity
@Table(name = "event")
@NoArgsConstructor
@Setter
@Getter
public class Event {

    public enum Type { DELETION }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private String actorEmailAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private String message;

    public Event(String actorEmailAddress, Type type, String message) {
        this.actorEmailAddress = actorEmailAddress;
        this.type = type;
        this.message = message;
    }

    @PrePersist
    private void prePersist() {
        created = LocalDateTime.now();
    }
}
