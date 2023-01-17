package uk.nhs.digital.uec.api.service;

import org.junit.Test;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link PagedQuery}
 */
public class PagedQueryTest {

    private static final UserSortCriteria DEFAULT_SORT_TYPE = UserSortCriteria.EMAIL_ADDRESS;

    @Test
    public void shouldConstruct() {
        // Given
        int pageNumber = 0;
        int resultsPerPage = 50;
        UserSortCriteria sortType = DEFAULT_SORT_TYPE;
        SortOrder sortOrder = SortOrder.ASCENDING;

        // When
        PagedQuery pagedQuery = new PagedQuery(pageNumber, resultsPerPage, Map.of(sortType, sortOrder),
                Collections.emptyMap());

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getSortCriteria().get(sortType), is(sortOrder));
    }

    @Test
    public void shouldFailToConstructGivenNegativePageNumber() {
        // Given
        int pageNumber = -1;

        try {
            // When
            new PagedQuery(pageNumber, 25, Map.of(DEFAULT_SORT_TYPE, SortOrder.ASCENDING), Collections.emptyMap());

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("pageNumber must be greater than or equal to 0"));
        }
    }

    @Test
    public void shouldFailToConstructGivenResultsPerPageTooSmall() {
        // Given
        int resultsPerPage = 4;

        try {
            // When
            new PagedQuery(0, resultsPerPage, Map.of(DEFAULT_SORT_TYPE, SortOrder.ASCENDING), Collections.emptyMap());

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("resultsPerPage must be greater than or equal to 5"));
        }
    }

    @Test
    public void shouldFailToConstructGivenResultsPerPageTooLarge() {
        // Given
        int resultsPerPage = 101;

        try {
            // When
            new PagedQuery(0, resultsPerPage, Map.of(DEFAULT_SORT_TYPE, SortOrder.ASCENDING), Collections.emptyMap());

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("resultsPerPage must be less than or equal to 100"));
        }
    }

    @Test
    public void shouldFailToConstructGivenNullSortCriteria() {
        // Given
        Map<UserSortCriteria, SortOrder> sortCriteria = null;

        try {
            // When
            new PagedQuery(0, 50, sortCriteria, Collections.emptyMap());

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("sortCriteria must not be null"));
        }
    }

    @Test
    public void shouldFailToConstructGivenNullFilterCriteria() {
        // Given
        Map<UserFilterCriteria, String> filterCriteria = null;

        try {
            // When
            new PagedQuery(0, 50, Map.of(DEFAULT_SORT_TYPE, SortOrder.ASCENDING), filterCriteria);

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("filterCriteria must not be null"));
        }
    }

}
