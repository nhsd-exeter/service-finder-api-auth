package uk.nhs.digital.uec.api.service.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;
import uk.nhs.digital.uec.api.testsupport.PagedQueryTestFactory;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Order;

/**
 * Tests for {@link UserQueryPageRequestFactory}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserQueryPageRequestFactoryTest {

    private static final UserSortCriteria SORT_TYPE = UserSortCriteria.EMAIL_ADDRESS;

    private static final int RESULTS_PER_PAGE = 5;

    @InjectMocks
    private UserQueryPageRequestFactory factory;

    @Test
    public void shouldCreatePageRequestGivenPageSize() {
        // Given
        int expectedResultsPerPage = 5;
        PagedQuery query = PagedQueryTestFactory.apagedQuery(expectedResultsPerPage, SORT_TYPE);

        // When
        PageRequest actual = factory.createPageRequest(query);

        // Then
        assertThat(actual.getPageSize(), is(expectedResultsPerPage));
    }

    @Test
    public void shouldCreatePageRequestGivenDescending() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(RESULTS_PER_PAGE, SORT_TYPE, SortOrder.DESCENDING);

        // When
        PageRequest actual = factory.createPageRequest(query);

        // Then
        Order firstOrder = actual.getSort().iterator().next();
        assertThat(firstOrder.getDirection(), is(Direction.DESC));
    }

    @Test
    public void shouldCreatePageRequestGivenAscending() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(RESULTS_PER_PAGE);

        // When
        PageRequest actual = factory.createPageRequest(query);

        // Then
        Order firstOrder = actual.getSort().iterator().next();
        assertThat(firstOrder.getDirection(), is(Direction.ASC));
    }

    @Test
    public void shouldCreatePageRequestGivenEmailAddress() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(RESULTS_PER_PAGE, SORT_TYPE);

        // When
        PageRequest actual = factory.createPageRequest(query);

        // Then
        Iterator<Order> iterator = actual.getSort().iterator();
        Order firstOrder = iterator.next();
        assertThat(firstOrder.getProperty(), is("emailAddress"));
    }

    @Test
    public void shouldCreatePageRequestGivenApprovalStatus() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(RESULTS_PER_PAGE, UserSortCriteria.APPROVAL_STATUS);

        // When
        PageRequest actual = factory.createPageRequest(query);

        // Then
        Iterator<Order> iterator = actual.getSort().iterator();
        Order firstOrder = iterator.next();
        assertThat(firstOrder.getProperty(), is("approvalStatus"));
    }

}
