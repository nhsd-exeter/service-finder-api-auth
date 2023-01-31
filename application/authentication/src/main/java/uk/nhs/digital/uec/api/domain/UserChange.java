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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;

/**
 * An individual that may register to use the Service Finder or Admin tool.
 */
@Entity
@Table(name = "user_change")
@NoArgsConstructor
@Setter
@Getter
public class UserChange {

    public enum FieldType {
        EMAIL_VERIFICATION,
        NAME,
        STATUS,
        STATE,
        REJECTION_REASON,
        ROLE,
        JOB_TITLE,
        JOB_TYPE,
        OTHER_JOB_TYPE,
        ORGANISATION_NAME,
        ORGANISATION_TYPE,
        OTHER_ORGANISATION_TYPE,
        TELEPHONE_NUMBER,
        POSTCODE,
        REGION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", referencedColumnName = "id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @JsonManagedReference
    private UserAccount updatedBy;

    @Column(nullable = false)
    private String originalValue;

    @Column(nullable = false)
    private String newValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType fieldType;

    public UserChange(User user,
                LocalDateTime updated,
                User updatedBy,
                String originalValue,
                String newValue,
                FieldType fieldType) {

        this.user = user.getUserAccount();
        this.updated = updated;
        this.updatedBy = updatedBy.getUserAccount();
        this.originalValue = originalValue;
        this.newValue = newValue;
        this.fieldType = fieldType;
    }

    public User getUser()
    {
        return this.user.convertToUser();
    }

    public void setUser(User user)
    {
        this.user = user.getUserAccount();
    }

}
