package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.SortedSet;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetail {

    public static final String OTHER = "OTHER";

    private String emailAddress;

    private boolean emailAddressVerified;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime registrationDate;

    @NotBlank(message = "name must not be blank")
    private String name;

    private String telephoneNumber;

    @NotBlank(message = "job title must not be blank")
    private String jobName;

    @NotBlank(message = "job role type must not be blank")
    private String jobType;

    private String jobTypeOther;

    @NotBlank(message = "organisation name must not be blank")
    private String organisationName;

    @NotBlank(message = "organisation type must not be blank")
    private String organisationType;

    private String organisationTypeOther;

    @NotBlank(message = "postcode must not be blank")
    private String postcode;

    @NotBlank(message = "region must not be empty")
    private String region;

    @NotEmpty(message = "approval status must not be blank")
    private String approvalStatus;

    private String rejectionReason;

    private SortedSet<String> roles;

    private LocalDateTime lastLoggedIn;

    private boolean inactive;

    @AssertTrue(message = "jobRoleTypeOther must not be set unless jobRoleType is OTHER")
    private boolean isJobTypeNotOtherAndNoDetailsProvided() {
        return isBlank(jobTypeOther) || OTHER.equals(jobType);
    }

    @AssertTrue(message = "jobRoleTypeOther must be set when jobRoleType is OTHER")
    private boolean isJobTypeOtherAndDetailsProvided() {
        return !OTHER.equals(jobType) || isNotBlank(jobTypeOther);
    }

    @AssertTrue(message = "organisationTypeOther must not be set unless organisationType is OTHER")
    private boolean isOrgTypeNotOtherAndNoDetailsProvided() {
        return isBlank(organisationTypeOther) || OTHER.equals(organisationType);
    }

    @AssertTrue(message = "organisationTypeOther must be set when organisationType is OTHER")
    private boolean isOrgTypeOtherAndDetailsProvided() {
        return !OTHER.equals(organisationType) || isNotBlank(organisationTypeOther);
    }

}
