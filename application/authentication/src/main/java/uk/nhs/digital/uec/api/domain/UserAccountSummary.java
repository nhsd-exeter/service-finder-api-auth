package uk.nhs.digital.uec.api.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A user account summary.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class UserAccountSummary {

    private Long id;
    private boolean emailAddressVerified;
    private String emailAddress;
    private LocalDateTime createdDate;
    private String userState;

}
