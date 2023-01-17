package uk.nhs.digital.uec.api.common.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.InputStreamSource;

@AllArgsConstructor
@Getter
public class MailInlineResource {

    private String contentId;

    private InputStreamSource inputStreamSource;

    private String contentType;

}
