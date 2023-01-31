package uk.nhs.digital.uec.api.common.mail;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.WebApplicationException;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


/**
 * The Java mail client used for SMTP email solutions.<br/>
 * For non-SMTP email use {@link AmazonSesMailClient}.
 */
@Component
@Profile("mail-smtp")
@RequiredArgsConstructor
public class JavaMailClient extends MailClient {

    private final JavaMailSender javaMailSender;

    @Override
    protected void sendMessage(String senderName,
                              String senderEmail,
                              String recipient,
                              String subject,
                              String bodyCopy,
                              boolean isHtml,
                              List<MailInlineResource> mailInlineResources) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            boolean mailContainsInlineResources = !mailInlineResources.isEmpty();
            if (mailContainsInlineResources) {
                helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            }
            helper.setFrom(new InternetAddress(senderEmail, senderName));
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setSentDate(new Date());
            helper.setText(bodyCopy, isHtml);
            if (mailContainsInlineResources) {
                addInlineResources(helper, mailInlineResources);
            }
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    protected void sendMessage(String senderName, String senderEmail, String recipient, String CC, String subject, String bodyCopy, boolean isHtml, List<MailInlineResource> mailInlineResources) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            boolean mailContainsInlineResources = !mailInlineResources.isEmpty();
            if (mailContainsInlineResources) {
                helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            }
            helper.setFrom(new InternetAddress(senderEmail, senderName));
            helper.setTo(recipient);
            helper.setCc(CC);
            helper.setSubject(subject);
            helper.setSentDate(new Date());
            helper.setText(bodyCopy, isHtml);
            if (mailContainsInlineResources) {
                addInlineResources(helper, mailInlineResources);
            }
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    private void addInlineResources(MimeMessageHelper helper, List<MailInlineResource> mailInlineResources) throws MessagingException {
        for (MailInlineResource mailInlineResource : mailInlineResources) {
            helper.addInline(mailInlineResource.getContentId(), mailInlineResource.getInputStreamSource(), mailInlineResource.getContentType());
        }
    }
}
