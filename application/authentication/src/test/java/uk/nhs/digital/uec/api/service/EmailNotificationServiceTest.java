package uk.nhs.digital.uec.api.service;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static uk.nhs.digital.uec.api.service.impl.EmailNotificationService.APPROVAL_SUBJECT;
import static uk.nhs.digital.uec.api.service.impl.EmailNotificationService.PASSWORD_RESET_SUBJECT;
import static uk.nhs.digital.uec.api.service.impl.EmailNotificationService.REJECTION_SUBJECT;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.common.mail.MailClient;
import uk.nhs.digital.uec.api.common.mail.Renderer;
import uk.nhs.digital.uec.api.common.mail.properties.MailProperties;
import uk.nhs.digital.uec.api.domain.ApprovalMessage;
import uk.nhs.digital.uec.api.domain.PasswordResetMessage;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.exception.NotificationException;
import uk.nhs.digital.uec.api.service.impl.EmailNotificationService;

/**
 * Tests for {@link EmailNotificationService}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationServiceTest {

  private static final String TEST_URL = "https://ui.servicefinder.test";

  private static final String SENDER_NAME = "The Sender";

  private static final String SENDER_EMAIL = "sender@example.com";

  private static final String RECIPIENT_EMAIL = "recipient@example.com";

  private static final String REJECTION_REASON = "bad data";

  private static final String APPROVER_NAME = "The Approver";

  private static final String APPROVER_EMAIL = "approver@example.com";

  private static final String USER_NAME = "John Smith";

  @Mock
  private MailClient mailClient;

  @Mock
  private Renderer renderer;

  @Mock
  private MailProperties mailProperties;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private EmailNotificationService emailNotificationService;

  @Before
  public void setUp() throws IOException, TemplateException {
    emailNotificationService =
      new EmailNotificationService(
        mailClient,
        renderer,
        mailProperties,
        TEST_URL
      );
    given(mailProperties.getSenderName()).willReturn(SENDER_NAME);
    given(mailProperties.getSenderEmail()).willReturn(SENDER_EMAIL);
    given(renderer.render(any(), any())).willReturn("email body");
  }

  @Test
  public void shouldSendApprovalMessage()
    throws IOException, TemplateException {
    // Given
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withName(USER_NAME)
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .createUser();

    // When
    emailNotificationService.sendApprovalMessage(user);

    // Then
    ArgumentCaptor<String> renderResultCaptor = ArgumentCaptor.forClass(
      String.class
    );
    verify(mailClient)
      .sendMessage(
        eq(SENDER_NAME),
        eq(SENDER_EMAIL),
        eq(user.getEmailAddress()),
        eq(APPROVAL_SUBJECT),
        renderResultCaptor.capture(),
        anyList()
      );

    ArgumentCaptor<ApprovalMessage> dataModelCaptor = ArgumentCaptor.forClass(
      ApprovalMessage.class
    );
    verify(renderer)
      .render(
        eq(EmailNotificationService.APPROVAL_TEMPLATE),
        dataModelCaptor.capture()
      );

    ApprovalMessage approvalMessage = dataModelCaptor.getValue();
    assertThat(approvalMessage.getTitle(), is(APPROVAL_SUBJECT));
    assertThat(approvalMessage.getApprover(), is(approver));
    assertThat(approvalMessage.getName(), is(USER_NAME));
    assertThat(approvalMessage.getReason(), is(nullValue()));
    assertThat(
      approvalMessage.getHomeUrl(),
      is(TEST_URL + EmailNotificationService.HOME_PAGE_URI)
    );
    assertThat(
      approvalMessage.getHelpUrl(),
      is(TEST_URL + EmailNotificationService.HELP_PAGE_URI)
    );
  }

  @Test
  public void shouldFailToSendApprovalMessageIfUserIsNull()
    throws IOException, TemplateException {
    // Given
    User user = null;

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("user must not be null");

    // When
    emailNotificationService.sendApprovalMessage(user);
  }

  @Test
  public void shouldSendRejectionMessage()
    throws IOException, TemplateException {
    // Given
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .withRejectionReason(REJECTION_REASON)
      .createUser();

    // When
    emailNotificationService.sendRejectionMessage(user);

    // Then
    ArgumentCaptor<String> renderResultCaptor = ArgumentCaptor.forClass(
      String.class
    );
    verify(mailClient)
      .sendMessage(
        eq(SENDER_NAME),
        eq(SENDER_EMAIL),
        eq(user.getEmailAddress()),
        eq(REJECTION_SUBJECT),
        renderResultCaptor.capture(),
        anyList()
      );

    ArgumentCaptor<ApprovalMessage> dataModelCaptor = ArgumentCaptor.forClass(
      ApprovalMessage.class
    );
    verify(renderer)
      .render(
        eq(EmailNotificationService.REJECTION_TEMPLATE),
        dataModelCaptor.capture()
      );

    ApprovalMessage approvalMessage = dataModelCaptor.getValue();
    assertThat(approvalMessage.getTitle(), is(REJECTION_SUBJECT));
    assertThat(approvalMessage.getApprover(), is(approver));
    assertThat(approvalMessage.getReason(), is(REJECTION_REASON));
    assertThat(
      approvalMessage.getHomeUrl(),
      is(TEST_URL + EmailNotificationService.HOME_PAGE_URI)
    );
    assertThat(
      approvalMessage.getHelpUrl(),
      is(TEST_URL + EmailNotificationService.HELP_PAGE_URI)
    );
  }

  @Test
  public void shouldFailToSendRejectionMessageIfUserIsNull()
    throws IOException, TemplateException {
    // Given
    User user = null;

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("user must not be null");

    // When
    emailNotificationService.sendRejectionMessage(user);
  }

  @Test
  public void shouldFailToSendRejectionMessageIfTemplateExceptionIsThrown()
    throws IOException, TemplateException {
    // Given
    doThrow(new TemplateException("failed", null))
      .when(renderer)
      .render(any(), any());
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .withRejectionReason(REJECTION_REASON)
      .createUser();

    // Expectations
    exceptionRule.expect(NotificationException.class);
    exceptionRule.expectMessage("Failed to send message");

    // When
    emailNotificationService.sendRejectionMessage(user);
  }

  @Test
  public void shouldFailToSendRejectionMessageIfIoExceptionIsThrown()
    throws IOException, TemplateException {
    // Given
    doThrow(new IOException("failed")).when(renderer).render(any(), any());
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .withRejectionReason(REJECTION_REASON)
      .createUser();

    // Expectations
    exceptionRule.expect(NotificationException.class);
    exceptionRule.expectMessage("Failed to send message");

    // When
    emailNotificationService.sendRejectionMessage(user);
  }

  @Test
  public void shouldSendPasswordResetMessage()
    throws IOException, TemplateException, ParseException {
    // Given
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .createUser();
    LocalDateTime passwordResetTime = LocalDateTime.of(
      2020,
      Month.MAY,
      25,
      11,
      30,
      27
    );

    // When
    emailNotificationService.sendSuccessfulPasswordResetMessage(
      user,
      passwordResetTime
    );

    // Then
    ArgumentCaptor<String> renderResultCaptor = ArgumentCaptor.forClass(
      String.class
    );
    verify(mailClient)
      .sendMessage(
        eq(SENDER_NAME),
        eq(SENDER_EMAIL),
        eq(user.getEmailAddress()),
        eq(PASSWORD_RESET_SUBJECT),
        renderResultCaptor.capture(),
        anyList()
      );

    ArgumentCaptor<PasswordResetMessage> dataModelCaptor = ArgumentCaptor.forClass(
      PasswordResetMessage.class
    );
    verify(renderer)
      .render(
        eq(EmailNotificationService.PASSWORD_RESET_TEMPLATE),
        dataModelCaptor.capture()
      );

    PasswordResetMessage passwordResetMessage = dataModelCaptor.getValue();
    assertThat(passwordResetMessage.getTitle(), is(PASSWORD_RESET_SUBJECT));
    assertThat(passwordResetMessage.getDate(), is("25/05/2020"));
    assertThat(passwordResetMessage.getTime(), is("11:30"));
    assertThat(
      passwordResetMessage.getHelpDesk(),
      is(EmailNotificationService.HELP_DESK_URL)
    );
  }

  @Test
  public void shouldFailToSendPasswordResetMessageIfUserIsNull()
    throws IOException, TemplateException {
    // Given
    User user = null;
    LocalDateTime passwordResetTime = LocalDateTime.of(
      2020,
      Month.MAY,
      25,
      11,
      30,
      27
    );

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("user must not be null");

    // When
    emailNotificationService.sendSuccessfulPasswordResetMessage(
      user,
      passwordResetTime
    );
  }

  @Test
  public void shouldFailToSendPasswordResetMessageIfDateTimeIsNull()
    throws IOException, TemplateException {
    // Given
    User approver = new UserBuilder()
      .withName(APPROVER_NAME)
      .withEmailAddress(APPROVER_EMAIL)
      .createUser();
    User user = new UserBuilder()
      .withEmailAddress(RECIPIENT_EMAIL)
      .withApprovalStatusUpdatedBy(approver)
      .createUser();
    LocalDateTime passwordResetTime = null;

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("date must not be null");

    // When
    emailNotificationService.sendSuccessfulPasswordResetMessage(
      user,
      passwordResetTime
    );
  }
}
