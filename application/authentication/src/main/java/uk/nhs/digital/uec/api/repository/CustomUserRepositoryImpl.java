package uk.nhs.digital.uec.api.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
/**
 * JPA implementation of complex {@link User user} queries involving pagination, sorting and filtering.
 */
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    protected final EntityManager entityManager;

    protected final CriteriaBuilder criteriaBuilder;

    protected final UserAccountRepository userAccountRepository;

    protected final UserDetailsRepository userDetailsRepository;

    @Autowired
    public CustomUserRepositoryImpl(EntityManager entityManager, UserAccountRepository userAccountRepository, UserDetailsRepository userDetailsRepository) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.userAccountRepository = userAccountRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<User> findByQuery(PageRequest pageRequest, Map<UserFilterCriteria, String> filterCriteria) {
        CheckArgument.isNotNull(pageRequest, "pageRequest must not be null");
        CheckArgument.isNotNull(filterCriteria, "filterCriteria must not be null");

        Long count = getCountForFullyRegisteredUsers(filterCriteria);

        List<User> users;
        if (count == 0) {
            users = new ArrayList<>();
        } else {
            users = getFullyRegisteredUsers((int) pageRequest.getOffset(), pageRequest.getPageSize(), pageRequest.getSort(), filterCriteria);
        }

        return new PageImpl<>(users, pageRequest, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByQuery(Map<UserFilterCriteria, String> filterCriteria) {
        CheckArgument.isNotNull(filterCriteria, "filterCriteria must not be null");

        List<User> users = new ArrayList<User>();
        List<User> usersFromQuery = getResultsForQuery(filterCriteria);
        if(!usersFromQuery.isEmpty()){
            users.addAll(usersFromQuery);
        }
        return users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserAccount> findUnregisteredUserAccounts(PageRequest pageRequest, Map<UserFilterCriteria, String> filterCriteria) {
        CheckArgument.isNotNull(pageRequest, "pageRequest must not be null");
        CheckArgument.isNotNull(filterCriteria, "filterCriteria must not be null");
        List<UserAccount> unRegisteredUsers = getUnRegisteredUsers((int) pageRequest.getOffset(), pageRequest.getPageSize(), pageRequest.getSort(), filterCriteria);
        return new PageImpl<>(unRegisteredUsers, pageRequest, getUnRegisteredUsersCount(filterCriteria));
    }


    private Long getCountForFullyRegisteredUsers(Map<UserFilterCriteria, String> filterCriteria) {
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserAccount> userAccountRoot = countCriteriaQuery.from(UserAccount.class);

        Subquery<UserDetails> userDetailsSubQuery = countCriteriaQuery.subquery(UserDetails.class);
        Root<UserDetails> userDetailsRoot = userDetailsSubQuery.from(UserDetails.class);
        userDetailsSubQuery.select(userDetailsRoot).where(criteriaBuilder.equal(userDetailsRoot.get("userAccount"), userAccountRoot.get("id")));

        Predicate[] filters = getPredicates(filterCriteria, userAccountRoot);
        List<Predicate> filterList = new ArrayList<>(Arrays.asList(filters));
        filterList.add(criteriaBuilder.exists(userDetailsSubQuery));
        filters = filterList.toArray(filters);

        countCriteriaQuery.select(criteriaBuilder.count(userAccountRoot))
                            .where(filters);

        return entityManager.createQuery(countCriteriaQuery)
                            .getSingleResult();
    }

    private List<User> getFullyRegisteredUsers(int offset, int pageSize, Sort sort, Map<UserFilterCriteria, String> filterCriteria) {
        CriteriaQuery<UserAccount> resultsCriteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        Root<UserAccount> userAccountRoot = resultsCriteriaQuery.from(UserAccount.class);

        Subquery<UserDetails> userDetailsSubQuery = resultsCriteriaQuery.subquery(UserDetails.class);
        Root<UserDetails> userDetailsRoot = userDetailsSubQuery.from(UserDetails.class);
        userDetailsSubQuery.select(userDetailsRoot).where(criteriaBuilder.equal(userDetailsRoot.get("userAccount"), userAccountRoot.get("id")));

        Predicate[] filters = getPredicates(filterCriteria, userAccountRoot);
        List<Predicate> filterList = new ArrayList<>(Arrays.asList(filters));
        filterList.add(criteriaBuilder.exists(userDetailsSubQuery));
        filters = filterList.toArray(filters);

        resultsCriteriaQuery.select(userAccountRoot)
                            .where(filters)
                            .orderBy(getOrders(sort, userAccountRoot));

        List<UserAccount> userAccounts = entityManager.createQuery(resultsCriteriaQuery)
                                                        .setFirstResult(offset)
                                                        .setMaxResults(pageSize)
                                                        .getResultList();
        ArrayList<User> users = new ArrayList<>();
        for (UserAccount userAccount : userAccounts) {
            users.add(userAccount.convertToUser());
        }
        return users;
    }


    private List<User> getResultsForQuery(Map<UserFilterCriteria, String> filterCriteria) {
        CriteriaQuery<UserAccount> resultsCriteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        Root<UserAccount> userRoot = resultsCriteriaQuery.from(UserAccount.class);
        resultsCriteriaQuery.select(userRoot)
                            .where(getPredicates(filterCriteria, userRoot));

        List<UserAccount> userAccounts = entityManager.createQuery(resultsCriteriaQuery)
                                                        .getResultList();

        ArrayList<User> users = new ArrayList<>();
        for (UserAccount userAccount : userAccounts) {
            users.add(userAccount.convertToUser());
        }
        return users;

    }

    private Predicate[] getPredicates(Map<UserFilterCriteria, String> filterCriteria, Root<UserAccount> root) {
        return filterCriteria.entrySet()
                            .stream()
                            .map((entry) -> getPredicateForFilter(entry.getKey(), entry.getValue(), root))
                            .toArray(Predicate[]::new);
    }

    private Predicate getPredicateForFilter(UserFilterCriteria field, String value, Root<UserAccount> userRoot) {
        switch (field.getFilterType()) {
            case BOOLEAN_VALUE:
                return criteriaBuilder.equal(userRoot.get(field.getName()), Boolean.valueOf(value));
            case STRING_EXACT_MATCH:
                return criteriaBuilder.equal(userRoot.get(field.getName()), value);
            case STRING_CASE_INSENSITIVE_LIKE:
                return criteriaBuilder.like(criteriaBuilder.lower(userRoot.get(field.getName())),
                                            criteriaBuilder.lower(criteriaBuilder.literal("%" + value + "%")));
            case JOIN_BY_CODE:
                return criteriaBuilder.equal(userRoot.join(field.getName()).get("code"), value);
            case LINKED_BOOLEAN_VALUE:
                return criteriaBuilder.equal(userRoot.get("userDetails").get(field.getName()), Boolean.valueOf(value));
            case LINKED_STRING_EXACT_MATCH:
                return criteriaBuilder.equal(userRoot.get("userDetails").get(field.getName()), value);
            case LINKED_STRING_CASE_INSENSITIVE_LIKE:
                return criteriaBuilder.like(criteriaBuilder.lower(userRoot.get("userDetails").get(field.getName())),
                                            criteriaBuilder.lower(criteriaBuilder.literal("%" + value + "%")));
            case LINKED_JOIN_BY_CODE:
                return criteriaBuilder.equal(userRoot.join("userDetails").join(field.getName()).get("code"), value);
            case DATE_EXACT_MATCH:
                Expression<String> dateStringExpr = criteriaBuilder.function("to_char", String.class,
                userRoot.get("created"), criteriaBuilder.literal("DD-MM-YYYY"));
                return criteriaBuilder.like(dateStringExpr, value);
            default:
                throw new IllegalArgumentException("Unsupported filter type for filter: " + field.getName());
        }
    }

    private List<Order> getOrders(Sort sort, Root<UserAccount> from) {
        return sort.stream()
                .map((order) -> translateOrder(order, from))
                .collect(Collectors.toList());
    }

    private Order translateOrder(Sort.Order order, Root<UserAccount> from) {
        Path<Object> field = null;
        if(UserAccount.hasField(order.getProperty())){
            field = from.get(order.getProperty());
        }else{
            field = from.get("userDetails").get(order.getProperty());
        }

        return order.isAscending() ? criteriaBuilder.asc(field) : criteriaBuilder.desc(field);
    }

    private List<UserAccount> getUnRegisteredUsers(int offset, int pageSize, Sort sort, Map<UserFilterCriteria, String> filterCriteria) {
        CriteriaQuery<UserAccount> resultsCriteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        Root<UserAccount> userAccountRoot = resultsCriteriaQuery.from(UserAccount.class);

        Subquery<UserDetails> userDetailsSubQuery = resultsCriteriaQuery.subquery(UserDetails.class);
        Root<UserDetails> userDetailsRoot = userDetailsSubQuery.from(UserDetails.class);
        userDetailsSubQuery.select(userDetailsRoot).where(criteriaBuilder.equal(userDetailsRoot.get("userAccount"), userAccountRoot.get("id")));
        Predicate[] filters = getPredicates(filterCriteria, userAccountRoot);
        List<Predicate> filterList = new ArrayList<>(Arrays.asList(filters));
        filterList.add(criteriaBuilder.not(criteriaBuilder.exists(userDetailsSubQuery)));
        filters = filterList.toArray(filters);

        resultsCriteriaQuery.select(userAccountRoot)
                            .where(filters)
                            .orderBy(getOrders(sort, userAccountRoot));

        return entityManager.createQuery(resultsCriteriaQuery)
                                                        .setFirstResult(offset)
                                                        .setMaxResults(pageSize)
                                                        .getResultList();
    }

    private Long getUnRegisteredUsersCount(Map<UserFilterCriteria, String> filterCriteria) {
        CriteriaQuery<Long> resultsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserAccount> userAccountRoot = resultsCriteriaQuery.from(UserAccount.class);

        Subquery<UserDetails> userDetailsSubQuery = resultsCriteriaQuery.subquery(UserDetails.class);
        Root<UserDetails> userDetailsRoot = userDetailsSubQuery.from(UserDetails.class);
        userDetailsSubQuery.select(userDetailsRoot).where(criteriaBuilder.equal(userDetailsRoot.get("userAccount"), userAccountRoot.get("id")));

        Predicate[] filters = getPredicates(filterCriteria, userAccountRoot);
        List<Predicate> filterList = new ArrayList<>(Arrays.asList(filters));
        filterList.add(criteriaBuilder.not(criteriaBuilder.exists(userDetailsSubQuery)));
        filters = filterList.toArray(filters);

        resultsCriteriaQuery.select(criteriaBuilder.count(userAccountRoot))
                            .where(filters);

        return entityManager.createQuery(resultsCriteriaQuery).getSingleResult();
    }
}
