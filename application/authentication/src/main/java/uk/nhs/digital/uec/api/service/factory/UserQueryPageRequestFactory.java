package uk.nhs.digital.uec.api.service.factory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create a {@link PageRequest} from a {@link PagedQuery}.
 */
public class UserQueryPageRequestFactory {

    public PageRequest createPageRequest(PagedQuery query) {
        List<Sort.Order> orders = query.getSortCriteria().entrySet().stream().map(this::getOrder).collect(Collectors.toList());
        return PageRequest.of(query.getPageNumber(), query.getResultsPerPage(), Sort.by(orders));
    }

    private Sort.Order getOrder(Map.Entry<UserSortCriteria, SortOrder> entry) {
        return new Sort.Order(SortOrder.DESCENDING.equals(entry.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC, entry.getKey().getName());
    }

}
