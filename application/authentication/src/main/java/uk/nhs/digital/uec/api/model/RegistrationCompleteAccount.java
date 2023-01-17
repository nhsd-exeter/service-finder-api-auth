package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Represents the data required to complete the user registration process.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCompleteAccount{

    public static final String OTHER = "OTHER";

    @NotBlank(message = "email address must not be blank")
    @Email(message = "email address must be valid")
    private String emailAddress;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "job title must not be blank")
    private String jobTitle;

    @NotBlank(message = "job role type must not be blank")
    private String jobRoleType;

    private String jobRoleTypeOtherDetails;

    @NotBlank(message = "organisation name must not be blank")
    private String organisationName;

    @NotBlank(message = "organisation type must not be blank")
    private String organisationType;

    private String organisationTypeOtherDetails;

    private String contactTelephoneNumber;

    @NotBlank(message = "workplacePostcode must not be blank")
    private String workplacePostcode;

    @NotEmpty(message = "region must not be empty")
    private String region;

    @AssertTrue(message = "acceptedTermsAndConditions must be true")
    private boolean acceptedTermsAndConditions;

    @NotNull(message = "approved must not be null")
    private boolean approved;

    @AssertTrue(message = "jobRoleTypeOther must not be set unless jobRoleType is OTHER")
    private boolean isJobTypeNotOtherAndNoDetailsProvided() {
        return isBlank(jobRoleTypeOtherDetails) || OTHER.equals(jobRoleType);
    }

    @AssertTrue(message = "jobRoleTypeOther must be set when jobRoleType is OTHER")
    private boolean isJobTypeOtherAndDetailsProvided() {
        return !OTHER.equals(jobRoleType) || isNotBlank(jobRoleTypeOtherDetails);
    }

    @AssertTrue(message = "organisationTypeOther must not be set unless organisationType is OTHER")
    private boolean isOrgTypeNotOtherAndNoDetailsProvided() {
        return isBlank(organisationTypeOtherDetails) || OTHER.equals(organisationType);
    }

    @AssertTrue(message = "organisationTypeOther must be set when organisationType is OTHER")
    private boolean isOrgTypeOtherAndDetailsProvided() {
        return !OTHER.equals(organisationType) || isNotBlank(organisationTypeOtherDetails);
    }
}
