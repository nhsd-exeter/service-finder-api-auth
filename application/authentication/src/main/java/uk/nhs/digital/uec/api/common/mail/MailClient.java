package uk.nhs.digital.uec.api.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.ws.rs.WebApplicationException;
import java.util.List;

@Slf4j
public abstract class MailClient {

    private EmailValidator validator = EmailValidator.getInstance();

    public boolean isValidEmailAddress(String emailAddress) {
        return isTestEmailAddress(emailAddress) || validator.isValid(emailAddress);
    }

    public boolean isTestEmailAddress(String emailAddress) {
        return emailAddress != null && emailAddress.endsWith("@example.com");
    }

    public void sendMessage(String senderName, String senderEmail, String recipient, String subject, String bodyCopy, List<MailInlineResource> mailInlineResources) {
        if (!isValidEmailAddress(senderEmail)) {
            throw new WebApplicationException("Invalid sender address");
        } else if (!isValidEmailAddress(recipient)) {
            throw new WebApplicationException("Invalid recipient address");
        } else if (isTestEmailAddress(recipient)) {
            log.warn("Recipient address is a test email address; not sending mail, but pretending I succeeded anyway.");
        } else {
            subject = subject == null ? "(no subject)" : subject;
            bodyCopy = bodyCopy == null ? "" : bodyCopy;
            String disguisedRecipient = recipient.substring(0, 1) + "..." + recipient.substring(recipient.indexOf('@'));
            try {
                sendMessage(senderName, senderEmail, recipient, subject, bodyCopy, isHtml(bodyCopy), mailInlineResources);
                log.info("Sent mail to " + disguisedRecipient);
            } catch (WebApplicationException e) {
                log.error("Failed to deliver mail to " + disguisedRecipient + ": " + e.getMessage());
                throw e;
            }
        }
    }

    public void sendMessage(String senderName, String senderEmail, String recipient, String CC, String subject, String bodyCopy, List<MailInlineResource> mailInlineResources) {
        if (!isValidEmailAddress(senderEmail)) {
            throw new WebApplicationException("Invalid sender address");
        } else if (!isValidEmailAddress(recipient) || !isValidEmailAddress(CC)) {
            throw new WebApplicationException("Invalid recipient/Carbon Copy address");
        } else if (isTestEmailAddress(recipient)) {
            log.warn("Recipient address is a test email address; not sending mail, but pretending I succeeded anyway.");
        } else {
            subject = subject == null ? "(no subject)" : subject;
            bodyCopy = bodyCopy == null ? "" : bodyCopy;
            String disguisedRecipient = recipient.substring(0, 1) + "..." + recipient.substring(recipient.indexOf('@'));
            try {
                sendMessage(senderName, senderEmail, recipient, CC, subject, bodyCopy, isHtml(bodyCopy), mailInlineResources);
                log.info("Sent mail to " + disguisedRecipient);
            } catch (WebApplicationException e) {
                log.error("Failed to deliver mail to " + disguisedRecipient + ": " + e.getMessage());
                throw e;
            }
        }
    }

    protected abstract void sendMessage(String senderName,
                                        String senderEmail,
                                        String recipient,
                                        String subject,
                                        String bodyCopy,
                                        boolean html,
                                        List<MailInlineResource> mailInlineResources);

    protected abstract void sendMessage(String senderName,
                                        String senderEmail,
                                        String recipient,
                                        String CCs,
                                        String subject,
                                        String bodyCopy,
                                        boolean html, List<MailInlineResource> mailInlineResources);


    private boolean isHtml(String bodyCopy) {
        int index = bodyCopy.indexOf('<');
        return index >= 0 && StringUtils.isBlank(bodyCopy.substring(0, index));
    }

}
