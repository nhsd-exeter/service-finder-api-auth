package uk.nhs.digital.uec.api.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * A paged result.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class PagedResult<T> extends PageImpl<T> {

    private final PagedQuery query;

    public PagedResult(List<T> results, Page<?> page, PagedQuery query) {
        super(results, page.getPageable(), page.getTotalElements());
        this.query = query;
    }

}
