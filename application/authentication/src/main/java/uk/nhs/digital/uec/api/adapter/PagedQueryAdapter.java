package uk.nhs.digital.uec.api.adapter;

import org.springframework.stereotype.Component;

import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PagedQueryAdapter {

    private static final Map<UserSortCriteria, SortOrder> DEFAULT_SORT_CRITERIA = Map.of(UserSortCriteria.EMAIL_ADDRESS, SortOrder.ASCENDING);

    public PagedQuery toPagedQuery(int pageNumber, int resultsPerPage, String filterCriteriaString, String sortCriteriaString) {
        CheckArgument.isGreaterThanOrEqualTo(pageNumber, 0, "pageNumber must be greater than or equal to 0");
        CheckArgument.isGreaterThanOrEqualTo(resultsPerPage, 5, "resultsPerPage must be greater than or equal to 5");
        CheckArgument.isLessThanOrEqualTo(resultsPerPage, 100, "resultsPerPage must be less than or equal to 100");

        Map<UserSortCriteria, SortOrder> sortCriteria = getSortCriteria(sortCriteriaString);
        Map<UserFilterCriteria, String> filterCriteria = getFilterCriteria(filterCriteriaString);
        return new PagedQuery(pageNumber, resultsPerPage, sortCriteria, filterCriteria);
    }

    private Map<UserSortCriteria, SortOrder> getSortCriteria(String sortCriteriaString) {
        Map<UserSortCriteria, SortOrder> sortCriteria = new LinkedHashMap<>();
        if (sortCriteriaString == null) {
            sortCriteria = DEFAULT_SORT_CRITERIA;
        } else {
            String[] fields = sortCriteriaString.split(",");
            for (String field : fields) {
                CheckArgument.matches(field, "[\\w]+[:](asc|desc)", "sort field \"" + field + "\" must match expression \"[\\w]+[:](asc|desc)\"");
                String[] fieldParams = field.split(":", 2);
                sortCriteria.put(UserSortCriteria.forName(fieldParams[0]), "desc".equals(fieldParams[1]) ? SortOrder.DESCENDING : SortOrder.ASCENDING);
            }
        }
        return sortCriteria;
    }

    private Map<UserFilterCriteria, String> getFilterCriteria(String filterCriteriaString) {
        Map<UserFilterCriteria, String> filterCriteria = new LinkedHashMap<>();
        if (filterCriteriaString != null) {
            String[] fields = filterCriteriaString.split(",");
            for (String field : fields) {
                CheckArgument.matches(field, "[\\w]+[:].*", "filter field \"" + field + "\" must match expression \"[\\w]+[:].*\"");
                String[] fieldParams = field.split(":", 2);
                filterCriteria.put(UserFilterCriteria.forName(fieldParams[0]), fieldParams[1]);
            }
        }
        return filterCriteria;
    }

}
