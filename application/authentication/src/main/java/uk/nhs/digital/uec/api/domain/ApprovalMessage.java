package uk.nhs.digital.uec.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class ApprovalMessage {

    @NotNull(message = "title must not be null")
    private String title;

    private User approver;

    private String reason;

    private String name;

    @NotNull(message = "homeUrl must not be null")
    private String homeUrl;

    @NotNull(message = "helpUrl must not be null")
    private String helpUrl;

}
