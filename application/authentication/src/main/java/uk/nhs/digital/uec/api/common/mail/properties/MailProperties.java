package uk.nhs.digital.uec.api.common.mail.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MailProperties {

    private final String senderName;

    private final String senderEmail;

    private final String recipientEmail;

}
