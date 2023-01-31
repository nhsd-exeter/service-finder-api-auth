package uk.nhs.digital.uec.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.time.LocalDateTime;

/**
 * A user summary.
 */
@AllArgsConstructor
@Getter
public class UserSummary {

    private final long id;

    private final String name;

    private final String emailAddress;

    private final boolean emailAddressVerified;

    private final String approvalStatus;

    private final Set<String> roles;

    private final String region;

    private final String organisationType;

    private final String jobType;

    private final LocalDateTime registrationDate;


}
