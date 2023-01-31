package uk.nhs.digital.uec.api.service.impl;

import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.common.mail.MailClient;
import uk.nhs.digital.uec.api.common.mail.MailInlineResource;
import uk.nhs.digital.uec.api.common.mail.Renderer;
import uk.nhs.digital.uec.api.config.MailProperties;
import uk.nhs.digital.uec.api.domain.ApprovalMessage;
import uk.nhs.digital.uec.api.domain.PasswordResetMessage;
import uk.nhs.digital.uec.api.domain.RegistrationPartIIReminderMessage;
import uk.nhs.digital.uec.api.domain.User;
    import uk.nhs.digital.uec.api.exception.NotificationException;
import uk.nhs.digital.uec.api.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

  public static final String APPROVAL_SUBJECT = "Start using NHS Service Finder";

  public static final String APPROVAL_TEMPLATE = "approve-registration.ftlh";

  public static final String HOME_PAGE_URI = "/";

  public static final String REJECTION_SUBJECT = "Account rejected for NHS Service Finder";

  public static final String REJECTION_TEMPLATE = "reject-registration.ftlh";

  public static final String HELP_PAGE_URI = "/help";

  public static final String PASSWORD_RESET_SUBJECT =
      "Password Reset: Service Finder account management";

  public static final String PASSWORD_RESET_TEMPLATE = "password-reset-confirmation.ftlh";

  public static final String PART2_REGISTRATION_REMINDER_SUBJECT =
      "NHS Service Finder - to complete the registration";
  public static final String PART2_REGISTRATION_REMINDER_HEADING =
      "Thank you for registering to use NHS Service Finder";
  public static final String PART2_REGISTRATION_REMINDER_PARAGRAPH_PART_ONE = "click here";
  public static final String PART2_REGISTRATION_REMINDER_PARAGRAPH_PART_TWO =
      "to complete the registration process";
  public static final String PART2_REGISTRATION_REMINDER_TEMPLATE =
      "part2-registration-reminder.ftlh";

  public static final String HELP_DESK_URL = "exeter.helpdesk@nhs.net";

  private final MailClient mailClient;

  private final Renderer renderer;

  private final MailProperties props;

  private final String serviceFinderUrl;

  @Autowired
  public EmailNotificationService(
      MailClient mailClient,
      Renderer renderer,
      MailProperties props,
      @Value("${servicefinder.url}") String serviceFinderUrl) {
    this.mailClient = mailClient;
    this.renderer = renderer;
    this.props = props;
    this.serviceFinderUrl = serviceFinderUrl;
  }

  /** {@inheritDoc} */
  @Override
  public void sendApprovalMessage(User user) {
    sendApprovalMessage(user, APPROVAL_SUBJECT, APPROVAL_TEMPLATE);
  }

  /** {@inheritDoc} */
  @Override
  public void sendRejectionMessage(User user) {
    sendApprovalMessage(user, REJECTION_SUBJECT, REJECTION_TEMPLATE);
  }

  @Override
  public void sendSuccessfulPasswordResetMessage(User user, LocalDateTime date) {
    sendPasswordResetMessage(user, PASSWORD_RESET_SUBJECT, PASSWORD_RESET_TEMPLATE, date);
  }

  /** {@inheritDoc} */
  @Override
  public void sendMessageForRegistrationPartII(User user) {
    sendMessageForRegistrationPartII(
        user, PART2_REGISTRATION_REMINDER_SUBJECT, PART2_REGISTRATION_REMINDER_TEMPLATE);
  }

  private void sendApprovalMessage(User user, String subject, String template) {
    CheckArgument.isNotNull(user, "user must not be null");
    log.debug("Sending email to user {} {}", user.getEmailAddress(), subject);
    ApprovalMessage approvalMessage =
        new ApprovalMessage(
            subject,
            user.getApprovalStatusUpdatedBy(),
            user.getRejectionReason(),
            user.getName(),
            serviceFinderUrl + HOME_PAGE_URI,
            serviceFinderUrl + HELP_PAGE_URI);
    this.sendMessage(user, subject, template, approvalMessage);
  }

  private void sendPasswordResetMessage(
      User user, String subject, String template, LocalDateTime date) {
    CheckArgument.isNotNull(user, "user must not be null");
    CheckArgument.isNotNull(date, "date must not be null");

    String time = date.format(DateTimeFormatter.ofPattern("HH:mm"));
    String day = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    log.debug("Sending email to user {} {}", user.getEmailAddress(), subject);

    PasswordResetMessage passwordResetMessage =
        new PasswordResetMessage(subject, day, time, HELP_DESK_URL);

    this.sendMessage(user, subject, template, passwordResetMessage);
  }

  private void sendMessageForRegistrationPartII(User user, String subject, String template) {
    CheckArgument.isNotNull(user, "user must not be null");
    String url =
        String.format(
            "%s/register/confirmUser/%s/%s",
            serviceFinderUrl, user.getEmailAddress(), getRandomNumberString());
    RegistrationPartIIReminderMessage registrationPartIIReminderMessage =
        new RegistrationPartIIReminderMessage(
            PART2_REGISTRATION_REMINDER_HEADING,
            url,
            PART2_REGISTRATION_REMINDER_PARAGRAPH_PART_ONE,
            PART2_REGISTRATION_REMINDER_PARAGRAPH_PART_TWO);
    this.sendMessage(user, subject, template, registrationPartIIReminderMessage);
  }

  private void sendMessage(User user, String subject, String template, Object message) {
    CheckArgument.isNotNull(user, "user must not be null");
    log.debug("Sending email to user {} {}", user.getEmailAddress(), subject);

    try {
      mailClient.sendMessage(
          props.getSenderName(),
          props.getSenderEmail(),
          user.getEmailAddress(),
          subject,
          renderer.render(template, message),
          List.of(
              new MailInlineResource(
                  "nhs-logo",
                  new ClassPathResource("templates/images/nhs-logo.gif"),
                  IMAGE_GIF_VALUE)));
    } catch (IOException | TemplateException e) {
      log.error("Failed to send message", e);
          throw new NotificationException("Failed to send message", e);

    }
  }

  private String getRandomNumberString() {
    Random rnd = new Random();
    int number = rnd.nextInt(999999);
    return String.format("%06d", number);
  }
}
