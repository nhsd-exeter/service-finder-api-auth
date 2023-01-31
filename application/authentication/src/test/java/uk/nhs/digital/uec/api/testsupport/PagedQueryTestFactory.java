package uk.nhs.digital.uec.api.testsupport;

import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;

import java.util.Collections;
import java.util.Map;

/**
 * Test factory for creating {@link PagedQuery}
 */
public class PagedQueryTestFactory {

    private static final Map<UserSortCriteria, SortOrder> DEFAULT_SORT_ORDER = Map.of(UserSortCriteria.EMAIL_ADDRESS,
            SortOrder.ASCENDING);

    public static PagedQuery apagedQuery(int resultsPerPage) {
        return new PagedQuery(0, resultsPerPage, DEFAULT_SORT_ORDER, Collections.emptyMap());
    }

    public static PagedQuery apagedQuery(int resultsPerPage, UserSortCriteria sortField) {
        return new PagedQuery(0, resultsPerPage, Map.of(sortField, SortOrder.DESCENDING), Collections.emptyMap());
    }

    public static PagedQuery apagedQuery(int resultsPerPage, UserSortCriteria sortField, SortOrder sortOrder) {
        return new PagedQuery(0, resultsPerPage, Map.of(sortField, sortOrder), Collections.emptyMap());
    }

}
