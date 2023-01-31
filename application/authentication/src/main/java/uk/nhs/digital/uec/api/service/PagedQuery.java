package uk.nhs.digital.uec.api.service;

import lombok.Data;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;

import java.io.Serializable;
import java.util.Map;

/**
 * A query for requesting paged, sorted and filtered data from a service.
 */
@Data
public class PagedQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int pageNumber;

    private final int resultsPerPage;

    private final Map<UserSortCriteria, SortOrder> sortCriteria;

    private final Map<UserFilterCriteria, String> filterCriteria;

    /**
     * @param pageNumber the page number for the pagination of results, must be a positive number
     * @param resultsPerPage the number of results per page for the pagination of results, must be a positive number
     * @param sortCriteria the sort criteria for the pagination of results, must not be null
     * @param filterCriteria the filter criteria for the pagination of results, must not be null
     */
    public PagedQuery(int pageNumber, int resultsPerPage, Map<UserSortCriteria, SortOrder> sortCriteria, Map<UserFilterCriteria, String> filterCriteria) {
        CheckArgument.isGreaterThanOrEqualTo(pageNumber, 0, "pageNumber must be greater than or equal to 0");
        CheckArgument.isGreaterThanOrEqualTo(resultsPerPage, 5, "resultsPerPage must be greater than or equal to 5");
        CheckArgument.isLessThanOrEqualTo(resultsPerPage, 100, "resultsPerPage must be less than or equal to 100");
        CheckArgument.isNotNull(filterCriteria, "filterCriteria must not be null");
        CheckArgument.isNotNull(sortCriteria, "sortCriteria must not be null");
        this.pageNumber = pageNumber;
        this.resultsPerPage = resultsPerPage;
        this.filterCriteria = filterCriteria;
        this.sortCriteria = sortCriteria;
    }

}
