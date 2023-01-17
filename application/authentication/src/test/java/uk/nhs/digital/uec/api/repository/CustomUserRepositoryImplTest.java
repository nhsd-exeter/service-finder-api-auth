package uk.nhs.digital.uec.api.repository;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SortOrder;
import uk.nhs.digital.uec.api.service.factory.UserQueryPageRequestFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserRepositoryImplTest {

    private static final Map<UserSortCriteria, SortOrder> DEFAULT_SORT_CRITERIA = Map.of(UserSortCriteria.EMAIL_ADDRESS,
            SortOrder.ASCENDING);

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Long> countCriteriaQuery;

    @Mock
    private Subquery<UserDetails> countCriteriaSubQuery;

    @Mock
    private Subquery<UserDetails> userDetailsSubQuery;

    @Mock
    private Root<UserAccount> userRootForCount;

    @Mock
    private Expression<Long> countExpression;

    @Mock
    private TypedQuery<Long> typedCountQuery;

    @Mock
    private CriteriaQuery<UserAccount> resultsCriteriaQuery;

    @Mock
    private CriteriaQuery<UserDetails> userDetailsCriteriaQuery;

    @Mock
    private Root<UserAccount> userRootForResults;

    @Mock
    private Root<UserDetails> userDetailsRoot;

    @Mock
    private TypedQuery<UserAccount> typedResultsQuery;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private Predicate userDetailsPredicate;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private UserQueryPageRequestFactory pageRequestFactory;

    private CustomUserRepositoryImpl repository;

    private Predicate[] predicateArray = new Predicate[1];

    @Before
    public void setUp() {
        final ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);

        // Criteria builder
        given(entityManager.getCriteriaBuilder()).willReturn(criteriaBuilder);

        // Count
        given(criteriaBuilder.createQuery(Long.class)).willReturn(countCriteriaQuery);

        given(countCriteriaQuery.from(UserAccount.class)).willReturn(userRootForCount);
        given(countCriteriaQuery.select(any())).willReturn(countCriteriaQuery);
        given(countCriteriaQuery.where(captor.capture())).willReturn(countCriteriaQuery);
        given(countCriteriaQuery.subquery(UserDetails.class)).willReturn(countCriteriaSubQuery);
        given(criteriaBuilder.count(userRootForCount)).willReturn(countExpression);
        given(entityManager.createQuery(countCriteriaQuery)).willReturn(typedCountQuery);

        // Results
        given(criteriaBuilder.createQuery(UserAccount.class)).willReturn(resultsCriteriaQuery);

        given(resultsCriteriaQuery.from(UserAccount.class)).willReturn(userRootForResults);
        given(resultsCriteriaQuery.select(any())).willReturn(resultsCriteriaQuery);
        given(resultsCriteriaQuery.where(captor.capture())).willReturn(resultsCriteriaQuery);
        given(resultsCriteriaQuery.orderBy(any(List.class))).willReturn(resultsCriteriaQuery);
        given(resultsCriteriaQuery.subquery(UserDetails.class)).willReturn(userDetailsSubQuery);
        given(userDetailsSubQuery.from(UserDetails.class)).willReturn(userDetailsRoot);
        given(userDetailsSubQuery.select(any(Expression.class))).willReturn(userDetailsSubQuery);

        given(entityManager.createQuery(resultsCriteriaQuery)).willReturn(typedResultsQuery);

        given(typedResultsQuery.setFirstResult(any(int.class))).willReturn(typedResultsQuery);
        given(typedResultsQuery.setMaxResults(any(int.class))).willReturn(typedResultsQuery);

        // User details results
        given(countCriteriaSubQuery.from(UserDetails.class)).willReturn(userDetailsRoot);
        given(countCriteriaSubQuery.select(any(Expression.class))).willReturn(countCriteriaSubQuery);

        given(criteriaBuilder.exists(countCriteriaSubQuery)).willReturn(userDetailsPredicate);
        given(criteriaBuilder.exists(userDetailsSubQuery)).willReturn(userDetailsPredicate);

        // Instantiate
        pageRequestFactory = new UserQueryPageRequestFactory();
        repository = new CustomUserRepositoryImpl(entityManager, userAccountRepository, userDetailsRepository);

        predicateArray[0] = userDetailsPredicate;
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, Collections.emptyMap());
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(1));
        given(typedResultsQuery.getResultList()).willReturn(List.of());

        // When
        repository.findByQuery(pageRequest, query.getFilterCriteria());

        // Then
        verify(countCriteriaQuery).select(countExpression);
        verify(countCriteriaQuery).where(predicateArray);

        verify(resultsCriteriaQuery).select(userRootForResults);
        verify(resultsCriteriaQuery).where(predicateArray);
        verify(resultsCriteriaQuery).orderBy(any(List.class));

        verify(typedResultsQuery).setFirstResult(0);
        verify(typedResultsQuery).setMaxResults(50);
    }

    @Test
    public void shouldFindByQueryWithoutPaging() {
        // Given
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, Collections.emptyMap());

        given(typedResultsQuery.getResultList()).willReturn(List.of());

        // When
        repository.findByQuery(query.getFilterCriteria());

        verify(resultsCriteriaQuery).select(userRootForResults);
        verify(resultsCriteriaQuery).where(any(Predicate[].class));
    }

    @Test
    public void shouldFindByQueryWithBooleanFilter() {
        // Given
        UserFilterCriteria filterField = UserFilterCriteria.EMAIL_ADDRESS_VERIFIED;
        boolean filterValue = true;
        Map<UserFilterCriteria, String> filterCriteria = Map.of(filterField, String.valueOf(filterValue));
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, filterCriteria);
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(1));
        given(typedResultsQuery.getResultList()).willReturn(List.of());

        Path<Object> path = mock(Path.class);
        given(userRootForCount.get(filterField.getName())).willReturn(path);
        given(userRootForResults.get(filterField.getName())).willReturn(path);
        Predicate predicate = mock(Predicate.class);
        given(criteriaBuilder.equal(path, filterValue)).willReturn(predicate);
        given(criteriaBuilder.exists(countCriteriaSubQuery)).willReturn(predicate);
        given(criteriaBuilder.exists(userDetailsSubQuery)).willReturn(predicate);

        // When
        repository.findByQuery(pageRequest, filterCriteria);

        // Then
        verify(userRootForCount).get(filterField.getName());
        verify(userRootForResults).get(filterField.getName());
        verify(criteriaBuilder, times(2)).equal(path, filterValue);

        verify(countCriteriaQuery).select(countExpression);
        assertThatWhereMethodReturnValueFor(countCriteriaQuery, is(predicate));

        verify(resultsCriteriaQuery).select(userRootForResults);
        assertThatWhereMethodReturnValueFor(resultsCriteriaQuery, is(predicate));
        verify(resultsCriteriaQuery).orderBy(any(List.class));

        verify(typedResultsQuery).setFirstResult(0);
        verify(typedResultsQuery).setMaxResults(50);
    }

    @Test
    public void shouldFindByQueryWithExactMatchFilter() {
        // Given
        UserFilterCriteria filterField = UserFilterCriteria.APPROVAL_STATUS;
        String filterValue = "PENDING";
        Map<UserFilterCriteria, String> filterCriteria = Map.of(filterField, filterValue);
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, filterCriteria);
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(1));
        given(typedResultsQuery.getResultList()).willReturn(List.of());

        Path<Object> path = mock(Path.class);
        given(path.get(filterField.getName())).willReturn(path);
        given(userRootForCount.get("userDetails")).willReturn(path);
        given(userRootForResults.get("userDetails")).willReturn(path);
        Predicate predicate = mock(Predicate.class);
        given(criteriaBuilder.equal(path, filterValue)).willReturn(predicate);
        given(criteriaBuilder.exists(countCriteriaSubQuery)).willReturn(predicate);
        given(criteriaBuilder.exists(userDetailsSubQuery)).willReturn(predicate);

        // When
        repository.findByQuery(pageRequest, filterCriteria);

        // Then
        verify(userRootForCount).get("userDetails");
        verify(userRootForResults).get("userDetails");
        verify(criteriaBuilder, times(2)).equal(path, filterValue);

        verify(countCriteriaQuery).select(countExpression);
        assertThatWhereMethodReturnValueFor(countCriteriaQuery, is(predicate));

        verify(resultsCriteriaQuery).select(userRootForResults);
        assertThatWhereMethodReturnValueFor(resultsCriteriaQuery, is(predicate));
        verify(resultsCriteriaQuery).orderBy(any(List.class));

        verify(typedResultsQuery).setFirstResult(0);
        verify(typedResultsQuery).setMaxResults(50);
    }

    @Test
    public void shouldFindByQueryWithCaseInsensitiveLikeFilter() {
        // Given
        UserFilterCriteria filterField = UserFilterCriteria.NAME;
        String filterValue = "Fred";
        Map<UserFilterCriteria, String> filterCriteria = Map.of(filterField, filterValue);
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, filterCriteria);
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(1));
        given(typedResultsQuery.getResultList()).willReturn(List.of());

        Path<Object> path = mock(Path.class);
        given(userRootForCount.get("userDetails")).willReturn(path);
        given(userRootForResults.get("userDetails")).willReturn(path);
        Expression<String> likeExpression = mock(Expression.class);
        given(criteriaBuilder.literal("%Fred%")).willReturn(likeExpression);
        Expression<String> lowerLikeExpression = mock(Expression.class);
        given(criteriaBuilder.lower(likeExpression)).willReturn(lowerLikeExpression);
        Expression<String> lowerPathExpression = mock(Expression.class);
        Path<String> stringPath = (Path<String>) ((Path<?>) path);
        Predicate predicate = mock(Predicate.class);
        given(criteriaBuilder.exists(countCriteriaSubQuery)).willReturn(predicate);
        given(criteriaBuilder.exists(userDetailsSubQuery)).willReturn(predicate);

        // When
        repository.findByQuery(pageRequest, filterCriteria);

        // Then
        verify(criteriaBuilder, times(2)).literal("%Fred%");
        verify(criteriaBuilder, times(2)).lower(likeExpression);

        verify(countCriteriaQuery).select(countExpression);
        assertThatWhereMethodReturnValueFor(countCriteriaQuery, is(predicate));

        verify(resultsCriteriaQuery).select(userRootForResults);
        assertThatWhereMethodReturnValueFor(resultsCriteriaQuery, is(predicate));
        verify(resultsCriteriaQuery).orderBy(any(List.class));

        verify(typedResultsQuery).setFirstResult(0);
        verify(typedResultsQuery).setMaxResults(50);
    }

    @Test
    public void shouldFindByQueryWithJoinFilter() {
        // Given
        UserFilterCriteria filterField = UserFilterCriteria.REGION;
        String filterValue = "SOUTH_WEST";
        Map<UserFilterCriteria, String> filterCriteria = Map.of(filterField, filterValue);
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, filterCriteria);
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(1));
        given(typedResultsQuery.getResultList()).willReturn(List.of());

        Join<Object, Object> join = mock(Join.class);
        given(userRootForCount.join("userDetails")).willReturn(join);
        given(userRootForResults.join("userDetails")).willReturn(join);
        given(join.join(filterField.getName())).willReturn(join);
        Path<Object> joinColumn = mock(Path.class);
        given(join.get("code")).willReturn(joinColumn);
        Predicate predicate = mock(Predicate.class);
        given(criteriaBuilder.equal(joinColumn, filterValue)).willReturn(predicate);
        given(criteriaBuilder.exists(countCriteriaSubQuery)).willReturn(predicate);
        given(criteriaBuilder.exists(userDetailsSubQuery)).willReturn(predicate);

        // When
        repository.findByQuery(pageRequest, filterCriteria);

        // Then
        verify(userRootForCount).join("userDetails");
        verify(userRootForResults).join("userDetails");
        verify(join, times(2)).get("code");
        verify(criteriaBuilder, times(2)).equal(joinColumn, filterValue);

        verify(countCriteriaQuery).select(countExpression);
        assertThatWhereMethodReturnValueFor(countCriteriaQuery, is(predicate));

        verify(resultsCriteriaQuery).select(userRootForResults);
        assertThatWhereMethodReturnValueFor(resultsCriteriaQuery, is(predicate));
        verify(resultsCriteriaQuery).orderBy(any(List.class));

        verify(typedResultsQuery).setFirstResult(0);
        verify(typedResultsQuery).setMaxResults(50);
    }

    @Test
    public void shouldFindByQueryWithZeroCount() {
        // Given
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, Collections.emptyMap());
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);

        given(typedCountQuery.getSingleResult()).willReturn(Long.valueOf(0));

        // When
        repository.findByQuery(pageRequest, query.getFilterCriteria());

        // Then
        verify(countCriteriaQuery).select(countExpression);

        verify(resultsCriteriaQuery, never()).select(userRootForResults);
        verify(resultsCriteriaQuery, never()).orderBy(any(List.class));

        verify(typedResultsQuery, never()).setFirstResult(0);
        verify(typedResultsQuery, never()).setMaxResults(50);
    }

    @Test
    public void shouldFailToFindByQueryGivenNullPageRequest() {
        // Given
        final PageRequest pageRequest = null;
        Map<UserFilterCriteria, String> filterCriteria = Map.of();

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("pageRequest must not be null");

        // When
        repository.findByQuery(pageRequest, filterCriteria);
    }

    @Test
    public void shouldFailToFindByQueryGivenNullFilterCriteria() {
        // Given
        PagedQuery query = new PagedQuery(0, 50, DEFAULT_SORT_CRITERIA, Collections.emptyMap());
        final PageRequest pageRequest = pageRequestFactory.createPageRequest(query);
        Map<UserFilterCriteria, String> filterCriteria = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("filterCriteria must not be null");

        // When
        repository.findByQuery(pageRequest, filterCriteria);
    }

    private void assertThatWhereMethodReturnValueFor(CriteriaQuery<?> criteriaQuery, Matcher<Object> matcher) {
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(criteriaQuery).where(captor.capture());
        Object captorValue = captor.getValue();
        assertThat(captorValue, matcher);
    }

}
