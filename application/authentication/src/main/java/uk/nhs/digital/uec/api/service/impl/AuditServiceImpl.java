package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.domain.Event;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.service.AuditService;

/** The {@link AuditService} default implementation. */
@Service
@Slf4j(topic = "Audit Service")
public class AuditServiceImpl implements AuditService {

  private final EventRepository eventRepository;

  @Autowired
  public AuditServiceImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /** {@inheritDoc} */
  @Override
  public void recordDeletion(User deletedUser, User actor) {
    log.info("Recording deletion event");
    Event event =
        new Event(
            actor.getEmailAddress(),
            Event.Type.DELETION,
            deletedUser.getEmailAddress() + " was deleted.");
    eventRepository.save(event);
  }
}
