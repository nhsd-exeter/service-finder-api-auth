package uk.nhs.digital.uec.api.adapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PagedQueryAdapterTest {

    private static final UserSortCriteria EMAIL_ADDRESS = UserSortCriteria.EMAIL_ADDRESS;

    private PagedQueryAdapter pagedQueryAdapter;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        pagedQueryAdapter = new PagedQueryAdapter();
    }

    @Test
    public void shouldGetPagedQueryGivenDefaultParams() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldGetPagedQueryGivenPageNumberIsVeryLarge() {
        // Given
        final int pageNumber = 100;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldFailToGetPagedQueryGivenPageNumberIsTooSmall() {
        // Given
        final int pageNumber = -1;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("pageNumber must be greater than or equal to 0");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldGetPagedQueryGivenResultsPerPageIsMinimum() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 5;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldGetPagedQueryGivenResultsPerPageIsMaximum() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 100;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldFailToGetPagedQueryGivenResultsPerPageIsTooSmall() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 4;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("resultsPerPage must be greater than or equal to 5");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailToGetPagedQueryGivenResultsPerPageIsTooLarge() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 101;
        final String filterCriteriaString = null;
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("resultsPerPage must be less than or equal to 100");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldGetPagedQueryGivenValidSingleSort() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name:asc";

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(UserSortCriteria.NAME), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldGetPagedQueryGivenValidMultipleSort() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name:asc,emailAddress:desc";

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria(), is(Collections.emptyMap()));
        assertThat(pagedQuery.getSortCriteria().get(UserSortCriteria.NAME), is(SortOrder.ASCENDING));
        assertThat(pagedQuery.getSortCriteria().get(UserSortCriteria.EMAIL_ADDRESS), is(SortOrder.DESCENDING));
        assertThat(pagedQuery.getSortCriteria().keySet().iterator().next(), is(UserSortCriteria.NAME));
    }

    @Test
    public void shouldFailGivenInvalidSortFieldFormat() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "a$b:asc";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"a$b:asc\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenInvalidSortFieldName() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "broken:asc";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("No UserSortCriteria matching field name 'broken'");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenMissingSortOrder() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"name\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenBlankSortOrder() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name:";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"name:\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenInvalidSortOrder() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name:junk";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"name:junk\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenBlankSort() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenOneBlankSortElement() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = null;
        final String sortCriteriaString = "name:asc,,emailAddress:desc";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("sort field \"\" must match expression \"[\\w]+[:](asc|desc)\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }


    @Test
    public void shouldGetPagedQueryGivenValidSingleFilter() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "name:foo";
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria().get(UserFilterCriteria.NAME), is("foo"));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldGetPagedQueryGivenValidMultipleFilter() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "name:foo,emailAddress:bar";
        final String sortCriteriaString = null;

        // When
        PagedQuery pagedQuery = pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);

        // Then
        assertThat(pagedQuery.getPageNumber(), is(pageNumber));
        assertThat(pagedQuery.getResultsPerPage(), is(resultsPerPage));
        assertThat(pagedQuery.getFilterCriteria().get(UserFilterCriteria.NAME), is("foo"));
        assertThat(pagedQuery.getFilterCriteria().get(UserFilterCriteria.EMAIL_ADDRESS), is("bar"));
        assertThat(pagedQuery.getFilterCriteria().keySet().iterator().next(), is(UserFilterCriteria.NAME));
        assertThat(pagedQuery.getSortCriteria().get(EMAIL_ADDRESS), is(SortOrder.ASCENDING));
    }

    @Test
    public void shouldFailGivenInvalidFilterFieldFormat() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "a$2:junk";
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("filter field \"a$2:junk\" must match expression \"[\\w]+[:].*\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenInvalidFilterFieldName() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "broken:junk";
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("No UserFilterCriteria matching field name 'broken'");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenMissingFilterValue() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "name";
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("filter field \"name\" must match expression \"[\\w]+[:].*\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenBlankFilter() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "";
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("filter field \"\" must match expression \"[\\w]+[:].*\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }

    @Test
    public void shouldFailGivenOneBlankFilterElement() {
        // Given
        final int pageNumber = 0;
        final int resultsPerPage = 50;
        final String filterCriteriaString = "name:foo,,emailAddress:bar";
        final String sortCriteriaString = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("filter field \"\" must match expression \"[\\w]+[:].*\"");

        // When
        pagedQueryAdapter.toPagedQuery(pageNumber, resultsPerPage, filterCriteriaString, sortCriteriaString);
    }


}
