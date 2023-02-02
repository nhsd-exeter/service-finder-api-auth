package uk.nhs.digital.uec.api.controller;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.exception.AccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.AccountNotApprovedException;
import uk.nhs.digital.uec.api.exception.ApiError;
import uk.nhs.digital.uec.api.exception.ApprovedAccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.ApprovedUserDeletionException;
import uk.nhs.digital.uec.api.exception.AuthenticationException;
import uk.nhs.digital.uec.api.exception.CognitoClientSecretHashException;
import uk.nhs.digital.uec.api.exception.EmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.ForgotPasswordBadEmailAddressException;
import uk.nhs.digital.uec.api.exception.InvalidCodeException;
import uk.nhs.digital.uec.api.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.exception.InvalidEntityCodeException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.MissingRejectionReasonException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.exception.NotificationException;
import uk.nhs.digital.uec.api.exception.RegistrationEmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.RegistrationExpiredCodeException;
import uk.nhs.digital.uec.api.exception.RegistrationInvalidCodeException;
import uk.nhs.digital.uec.api.exception.RejectionReasonUpdateException;
import uk.nhs.digital.uec.api.exception.UserIncompleteException;
import uk.nhs.digital.uec.api.exception.UserManagementException;
import uk.nhs.digital.uec.api.exception.UserNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice which processes exceptions in user management service
 */
@Slf4j
@ControllerAdvice
public class UserManagementControllerAdvice extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<String> validationMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "There are validation errors", validationMessages);
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(AccountNotApprovedException.class)
  protected ResponseEntity<Object> handleAccountNotApprovedException(AccountNotApprovedException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(InvalidRegistrationDetailsException.class)
  protected ResponseEntity<Object> handleInvalidRegistrationDetailsException(InvalidRegistrationDetailsException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrors());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({ InvalidCredentialsException.class, InvalidLoginException.class })
  protected ResponseEntity<Object> handleInvalidLoginCredentialsException(Exception ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid credentials", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(InvalidCodeException.class)
  protected ResponseEntity<Object> handleInvalidCodeException(InvalidCodeException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid code", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(InvalidEntityCodeException.class)
  protected ResponseEntity<Object> handleInvalidEntityCodeException(InvalidCodeException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ExpiredCodeException.class)
  protected ResponseEntity<Object> handleExpiredCodeException(ExpiredCodeException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Code expired", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Not authorized to complete this action",
        new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(UserIncompleteException.class)
  protected ResponseEntity<Object> handleUserIncompleteException(UserIncompleteException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Incomplete registration", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(EmailAddressNotRegisteredException.class)
  protected ResponseEntity<Object> handleEmailAddressNotRegisteredException(EmailAddressNotRegisteredException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Email address not registered", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(MissingRejectionReasonException.class)
  protected ResponseEntity<Object> handleMissingRejectionReasonException(MissingRejectionReasonException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Rejection reason missing", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(RejectionReasonUpdateException.class)
  protected ResponseEntity<Object> handleRejectionReasonUpdateException(RejectionReasonUpdateException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Rejection reason supplied but this is not a rejection",
        new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ApprovedUserDeletionException.class)
  protected ResponseEntity<Object> handleApprovedUserDeletionException(ApprovedUserDeletionException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Approved users cannot be deleted", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(UserManagementException.class)
  protected ResponseEntity<Object> handleUserManagementException(UserManagementException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(NoRolesException.class)
  protected ResponseEntity<Object> handleNoRolesException(NoRolesException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "No roles, no access", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(Exception ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(UserNotFoundException.class)
  protected ResponseEntity<Object> handleUserSynchronizationException(UserNotFoundException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(CognitoClientSecretHashException.class)
  protected ResponseEntity<Object> handleCognitoClientSecretHashException(CognitoClientSecretHashException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate secret hash",
        new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = { AccessDeniedException.class })
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(NotificationException.class)
  protected ResponseEntity<Object> handleNotificationException(NotificationException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(ForgotPasswordBadEmailAddressException.class)
  protected ResponseEntity<Object> handleForgotPasswordBadEmailAddressException(
      ForgotPasswordBadEmailAddressException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.OK, "", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "", new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(AccountAlreadyRegisteredException.class)
  protected ResponseEntity<Object> handleAccountAlreadyRegisteredException(AccountAlreadyRegisteredException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ApprovedAccountAlreadyRegisteredException.class)
  protected ResponseEntity<Object> handleApprovedAccountAlreadyRegisteredException(
      ApprovedAccountAlreadyRegisteredException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(RegistrationExpiredCodeException.class)
  protected ResponseEntity<Object> handleRegistrationExpiredCodeException(RegistrationExpiredCodeException ex,
      WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(RegistrationInvalidCodeException.class)
  protected ResponseEntity<Object> handleInvalidCodeException(RegistrationInvalidCodeException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(RegistrationEmailAddressNotRegisteredException.class)
  protected ResponseEntity<Object> handleRegistrationEmailAddressNotRegisteredException(
      RegistrationEmailAddressNotRegisteredException ex, WebRequest request) {
    log.error(ExceptionUtils.getStackTrace(ex));
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), new ArrayList<>());
    return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

}
