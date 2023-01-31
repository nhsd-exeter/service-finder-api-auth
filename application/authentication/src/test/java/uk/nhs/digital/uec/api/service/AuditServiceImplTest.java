package uk.nhs.digital.uec.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.domain.Event;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.repository.EventRepository;
import uk.nhs.digital.uec.api.service.impl.AuditServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceImplTest {

    private static final String actorEmailAddress = "actor@gmail.com";

    private static final String userForDeletionEmailAddress = "userForDeletion@gmail.com";

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private AuditServiceImpl auditServiceImpl;

    @Test
    public void shouldPassCorrectlyConstructedEventIntoRepository() {
        // Given
        User actor = new User();
        actor.setEmailAddress(actorEmailAddress);
        User userForDeletion = new User();
        userForDeletion.setEmailAddress(userForDeletionEmailAddress);

        // When
        auditServiceImpl.recordDeletion(userForDeletion, actor);

        // Then
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(Event.Type.DELETION);
        assertThat(savedEvent.getActorEmailAddress()).isEqualTo(actorEmailAddress);
        assertThat(savedEvent.getMessage()).isEqualTo(userForDeletionEmailAddress + " was deleted.");
    }

}
