package uk.nhs.digital.uec.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class PasswordResetMessage {

    @NotNull(message = "title must not be null")
    private String title;

    @NotNull(message = "date must not be null")
    private String date;

    @NotNull(message = "time must not be null")
    private String time;

    @NotNull(message = "helpDesk must not be null")
    private String helpDesk;

}
