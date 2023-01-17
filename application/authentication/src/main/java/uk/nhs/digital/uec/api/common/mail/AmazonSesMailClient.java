package uk.nhs.digital.uec.api.common.mail;

import static javax.mail.Message.RecipientType;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The Amazon SES mail client used for non-SMTP email solutions. For SMTP email use {@link
 * JavaMailClient}.
 */
@Component
@Configuration
@Profile("!mail-smtp")
@RequiredArgsConstructor
@Slf4j
public class AmazonSesMailClient extends MailClient {

  private static final Regions DEFAULT_SES_REGION = Regions.EU_WEST_1;

  private final AmazonSimpleEmailService client;

  // @Autowired private Environment environment;

  public AmazonSesMailClient() {
    // if (isInLocalEnvironment()) {
    //    log.info("Running local AWS STS Authentication");
    //   this.client =
    //       AmazonSimpleEmailServiceClientBuilder.standard().withRegion(getSesRegion()).build();
    //  } else {
    this.client =
        AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(getSesRegion())
            .withCredentials(new InstanceProfileCredentialsProvider(false))
            .build();
    // }
  }

  @Override
  public void sendMessage(
      String senderName,
      String senderEmail,
      String recipient,
      String subject,
      String bodyCopy,
      boolean isHtml,
      List<MailInlineResource> mailInlineResources) {
    try {
      Content content = new Content().withCharset(StandardCharsets.UTF_8.name()).withData(bodyCopy);
      Body body = isHtml ? new Body().withHtml(content) : new Body().withText(content);
      String source = String.format("%s <%s>", senderName, senderEmail);

      if (mailInlineResources.isEmpty()) {
        sendEmail(recipient, subject, body, source);
      } else {
        sendEmailWithInlineResources(recipient, subject, body, source, mailInlineResources);
      }

    } catch (Exception e) {
      log.error("sendEmail / sendEmailWithInlineResources failed", e);
      throw new WebApplicationException(e);
    }
  }

  @Override
  protected void sendMessage(
      String senderName,
      String senderEmail,
      String recipient,
      String CC,
      String subject,
      String bodyCopy,
      boolean isHtml,
      List<MailInlineResource> mailInlineResources) {
    try {
      Content content = new Content().withCharset(StandardCharsets.UTF_8.name()).withData(bodyCopy);
      Body body = isHtml ? new Body().withHtml(content) : new Body().withText(content);
      String source = String.format("%s <%s>", senderName, senderEmail);

      if (mailInlineResources.isEmpty()) {
        sendEmail(recipient, CC, subject, body, source);
      } else {
        sendEmailWithInlineResources(recipient, CC, subject, body, source, mailInlineResources);
      }

    } catch (Exception e) {
      log.error("sendEmail / sendEmailWithInlineResources failed", e);
      throw new WebApplicationException(e);
    }
  }

  /* private boolean isInLocalEnvironment() {
    return Arrays.stream(environment.getActiveProfiles())
        .anyMatch(env -> env.equals("local") || env.equals("test"));
  }*/

  private void sendEmailWithInlineResources(
      String recipient,
      String subject,
      Body body,
      String source,
      List<MailInlineResource> mailInlineResources)
      throws MessagingException, IOException {

    Session session = Session.getInstance(new Properties());
    MimeMessage message = new MimeMessage(session);
    message.setFrom((new InternetAddress(source)));
    message.setRecipients(RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(subject);

    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(body.getHtml().getData(), MediaType.TEXT_HTML);

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);
    addInlineResources(multipart, mailInlineResources);
    message.setContent(multipart);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    message.writeTo(outputStream);

    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
    client.sendRawEmail(rawEmailRequest);
  }

  private void addInlineResources(Multipart multipart, List<MailInlineResource> mailInlineResources)
      throws MessagingException, IOException {
    for (MailInlineResource mailInlineResource : mailInlineResources) {
      MimeBodyPart resourcePart = new MimeBodyPart();
      DataSource dataSource =
          new ByteArrayDataSource(
              mailInlineResource.getInputStreamSource().getInputStream(),
              mailInlineResource.getContentType());
      resourcePart.setDataHandler(new DataHandler(dataSource));
      resourcePart.setHeader(
          "Content-ID", String.format("<%s>", mailInlineResource.getContentId()));
      resourcePart.setDisposition(MimeBodyPart.INLINE);
      multipart.addBodyPart(resourcePart);
    }
  }

  private void sendEmail(String recipient, String subject, Body body, String source) {
    SendEmailRequest request =
        new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(recipient))
            .withMessage(
                new Message()
                    .withBody(body)
                    .withSubject(
                        new Content().withCharset(StandardCharsets.UTF_8.name()).withData(subject)))
            .withSource(source);
    client.sendEmail(request);
  }

  private void sendEmail(String recipient, String CC, String subject, Body body, String source) {
    SendEmailRequest request =
        new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(recipient).withCcAddresses(CC))
            .withMessage(
                new Message()
                    .withBody(body)
                    .withSubject(
                        new Content().withCharset(StandardCharsets.UTF_8.name()).withData(subject)))
            .withSource(source);
    client.sendEmail(request);
  }

  private void sendEmailWithInlineResources(
      String recipient,
      String CC,
      String subject,
      Body body,
      String source,
      List<MailInlineResource> mailInlineResources)
      throws MessagingException, IOException {

    Session session = Session.getInstance(new Properties());
    MimeMessage message = new MimeMessage(session);
    message.setFrom((new InternetAddress(source)));
    message.setRecipients(RecipientType.TO, InternetAddress.parse(recipient));
    message.addRecipients(RecipientType.CC, InternetAddress.parse(CC));
    message.setSubject(subject);
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(body.getHtml().getData(), MediaType.TEXT_HTML);

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);
    addInlineResources(multipart, mailInlineResources);
    message.setContent(multipart);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    message.writeTo(outputStream);

    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
    client.sendRawEmail(rawEmailRequest);
  }

  private Regions getSesRegion() {
    String regionName = System.getenv("SES_REGION");
    Regions sesRegion;
    try {
      sesRegion = Regions.fromName(regionName);
    } catch (IllegalArgumentException e) {
      log.warn(
          "Cannot set region to '{}'; using default '{}'.",
          regionName,
          DEFAULT_SES_REGION.getName());
      sesRegion = DEFAULT_SES_REGION;
    }
    log.info("SES region = " + sesRegion.getDescription());
    return sesRegion;
  }
}
