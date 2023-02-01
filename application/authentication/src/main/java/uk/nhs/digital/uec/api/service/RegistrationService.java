package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.model.ApprovalStatus;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;

/**
 * Interface to encapsulate the business logic for the service finder registration process.
 */
public interface RegistrationService {

    /**
     * The name of the UUID attribute that is created in Cognito for the users.
     */
    static final String USER_ATTRIBUTE_SUB = "sub";

    /**
     * Method to invoke the account request process. This method will create a user account
     * in Cognito for Service Finder if one does not already exist.
     *
     * @param requestAccount {@link RegistrationRequestAccount}
     */
    void requestAccount(final RegistrationRequestAccount requestAccount);

    /**
     * Method to verify the account by calling Cognito.
     *
     * @param emailVerification {@link EmailVerification}
     *
     * @return {@ApprovalStatus} of the account.
     */
    ApprovalStatus verifyAccount(final EmailVerification emailVerification);

    /**
     * Method to invoke the account request completion process. This process involves
     * creating the user account in the Service Finder database. Stages in this method are:
     *      verify email address in Cognito
     *      update approval status and role if email address is in the approved domain list
     *      store the user in the service finder database
     *
     * @param completeAccount {@link RegistrationCompleteAccount}
     */
    void completeRegistration(final RegistrationCompleteAccount completeAccount);

}
