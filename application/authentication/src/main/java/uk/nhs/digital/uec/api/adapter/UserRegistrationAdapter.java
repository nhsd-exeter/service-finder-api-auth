package uk.nhs.digital.uec.api.adapter;

import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_APPROVED;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_PENDING;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.JobType;
import uk.nhs.digital.uec.api.domain.OrganisationType;
import uk.nhs.digital.uec.api.domain.Region;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.exception.AccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidEntityCodeException;
import uk.nhs.digital.uec.api.exception.UserAccountMissingWhenCompletingRegistrationException;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.UserRegistration;
import uk.nhs.digital.uec.api.repository.JobTypeRepository;
import uk.nhs.digital.uec.api.repository.OrganisationTypeRepository;
import uk.nhs.digital.uec.api.repository.RegionRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;


/**
 * Adapter class for converting a web tier {@link UserRegistration} objects into domain objects.
 */
@Component
@AllArgsConstructor
public class UserRegistrationAdapter {

    private final RegionRepository regionRepository;

    private final JobTypeRepository jobTypeRepository;

    private final OrganisationTypeRepository organisationTypeRepository;

    private final UserRepository userRepository;

    public User toUser(RegistrationCompleteAccount completeAccount) {

        CheckArgument.isNotNull(completeAccount, "completeAccount must be set");

        // Fetch existing user from DB
        Optional<User> userToRegisterOptional = userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(completeAccount.getEmailAddress());

        if(userToRegisterOptional.isPresent()){
            JobType jobType = jobTypeRepository.findByCode(completeAccount.getJobRoleType()).orElseThrow(() -> new InvalidEntityCodeException("Invalid job type code: " + completeAccount.getJobRoleType()));
            OrganisationType organisationType = organisationTypeRepository.findByCode(completeAccount.getOrganisationType()).orElseThrow(() -> new InvalidEntityCodeException("Invalid org type code: " + completeAccount.getOrganisationType()));
            Region region = null;
            String regionCode = completeAccount.getRegion();
            if (regionCode != null) {
                region = regionRepository.findByCode(completeAccount.getRegion()).orElseThrow(() -> new InvalidEntityCodeException("Invalid region code: " + completeAccount.getRegion()));
            }

            String approval_status = APPROVAL_STATUS_PENDING;
            if(completeAccount.isApproved())
            {
                approval_status = APPROVAL_STATUS_APPROVED;
            }

            SortedSet<Role> roleSet = new TreeSet<>();

            User userToRegister = userToRegisterOptional.get();
            if(userToRegister.getUserDetails() != null){
                throw new AccountAlreadyRegisteredException(completeAccount.getEmailAddress());
            }

            UserDetails userDetails = new UserDetails();
            userDetails.setApprovalStatus(approval_status);
            userDetails.setApprovalStatusUpdated(LocalDateTime.now());
            userDetails.setApprovalStatusUpdatedBy(userToRegister.getUserAccount());
            userDetails.setCreated(LocalDateTime.now());
            userDetails.setJobName(completeAccount.getJobTitle());
            userDetails.setJobType(jobType);
            userDetails.setJobTypeOther(completeAccount.getJobRoleTypeOtherDetails());
            userDetails.setName(completeAccount.getName());
            userDetails.setOrganisationName(completeAccount.getOrganisationName());
            userDetails.setOrganisationType(organisationType);
            userDetails.setOrganisationTypeOther(completeAccount.getOrganisationTypeOtherDetails());
            userDetails.setPostcode(completeAccount.getWorkplacePostcode());
            userDetails.setRegion(region);
            userDetails.setRoles(roleSet);
            userDetails.setTelephoneNumber(completeAccount.getContactTelephoneNumber());
            userDetails.setTermsAndConditionsAccepted(LocalDateTime.now());
            userDetails.setUserAccount(userToRegister.getUserAccount());

            userToRegister.setUserDetails(userDetails);
            userToRegister.setUserState(User.USER_STATE_ACTIVE);
            userToRegister.setInactiveDate(null);

            return userToRegister;

        }else{
            throw new UserAccountMissingWhenCompletingRegistrationException("Account : " + completeAccount.getEmailAddress() + "is missing a user_account entry");
        }
    }

}
