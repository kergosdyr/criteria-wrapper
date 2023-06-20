package org.kibo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import javax.persistence.criteria.Selection;
import org.kibo.aggregation.Aggregation;
import org.kibo.where.WhereCondition;

@SuppressWarnings("rawtypes")
public class QueryFactory<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private CriteriaQuery criteriaQuery;
    private final Root<T> root;

    private int offset = 0;
    private int limit = Integer.MAX_VALUE;

    public QueryFactory(
        EntityManager em,
        Class<T> clazz
    ) {
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(clazz);
        this.root = criteriaQuery.from(clazz);
    }
    <R> QueryFactory(
        EntityManager em,
        CriteriaBuilder cb,
        CriteriaQuery cq,
        Root root,
        Class<T> clazz
    ) {
        this.entityManager = em;
        this.criteriaBuilder = cb;
        this.criteriaQuery = cq;
        this.root = root;
    }

    <R> QueryFactory(
        EntityManager em,
        Class<T> clazz,
        Class<R> projectionClass
    ) {
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(projectionClass);
        this.root = criteriaQuery.from(clazz);
    }

    public static QueryFactoryBuilder db(EntityManager entityManager) {
        return new QueryFactoryBuilder(entityManager);
    }

    private QueryFactory(
        QueryFactory<T> queryFactory,
        Root<T> root
    ) {
        this.entityManager = queryFactory.entityManager;
        this.criteriaBuilder = queryFactory.criteriaBuilder;
        this.criteriaQuery = queryFactory.criteriaQuery;
        this.root = root;
    }

    public QueryFactory<T> select(Class<T> clazz) {
        criteriaQuery = criteriaBuilder.createQuery(clazz);
        criteriaQuery.select(criteriaQuery.from(clazz));
        return this;
    }

    <R> QueryFactory<R> selectMulti(
        Class<R> projectionClass,
        List<Selector> selectors
    ) {

        criteriaQuery = criteriaQuery.select(criteriaBuilder.construct(
            projectionClass,
            selectors.stream()
                .map(selector -> selector.toSelection(this.criteriaBuilder, this.root))
                .toArray(Selection[]::new)
        ));

        return new QueryFactory<R>(
            this.entityManager, this.criteriaBuilder, this.criteriaQuery, this.root,
            projectionClass
        );


    }

    public QueryFactory<T> from(Class<T> clazz) {
        return this;
    }

    public QueryFactory<T> where(WhereCondition... condition) {
        criteriaQuery.where(
            criteriaBuilder.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public QueryFactory<T> whereAnd(WhereCondition... condition) {
        criteriaQuery.where(
            criteriaBuilder.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public QueryFactory<T> whereOr(WhereCondition... condition) {
        criteriaQuery.where(
            criteriaBuilder.or(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public QueryFactory<T> join(
        Column column,
        OnConditions onConditions
    ) {
        Join<?, ?> join = root.join(column.getFieldName(), JoinType.INNER);
        List<WhereCondition> conditions = onConditions.getConditions();
        criteriaQuery.where(criteriaBuilder.and(getPredicateFromCondition(join, conditions)));
        return this;
    }

    private Predicate[] getPredicateFromCondition(
        From<?, ?> from,
        List<WhereCondition> conditions
    ) {
        return conditions.stream().map(condition -> condition != null
            ? condition.toPredicate(from, criteriaBuilder, criteriaQuery)
            : criteriaBuilder.conjunction()).toArray(Predicate[]::new
        );
    }

    public QueryFactory<T> join(Column column) {
        root.join(column.getFieldName(), JoinType.INNER);
        return this;
    }

    public QueryFactory<T> fetch(Column column) {
        root.fetch(column.getFieldName());
        return this;
    }

    public <K> QueryFactory<T> join(
        Class<K> joinClass,
        OnConditions onConditions
    ) {

        criteriaQuery.where(criteriaBuilder.and(onConditions.getConditions()
            .stream()
            .map(condition -> condition.toPredicate(root, criteriaBuilder, criteriaQuery))
            .toArray(Predicate[]::new)));
        return this;
    }

    public QueryFactory<T> having(WhereCondition condition) {
        criteriaQuery.having(condition.toPredicate(root, criteriaBuilder, criteriaQuery));
        return this;
    }

    public QueryFactory<T> limit(
        int offset,
        int limit
    ) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    public List<T> getResultList() {
        return entityManager.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(limit)
            .getResultList();
    }

    public Optional<T> getResult() {
        return entityManager.createQuery(criteriaQuery).setFirstResult(offset).getResultList()
            .stream().findFirst();
    }

    public QueryFactory<T> groupBy(Column... columns) {
        List<Expression<?>> groupByList = new ArrayList<>();
        for (Column column : columns) {
            groupByList.add(root.get(column.getFieldName()));
        }
        criteriaQuery.groupBy(groupByList);
        return this;
    }

    public QueryFactory<T> orderBy(
        OrderDirection direction,
        Column... columns
    ) {
        List<Order> orderList = new ArrayList<>();
        for (Column column : columns) {
            switch (direction) {
                case ASC:
                    orderList.add(criteriaBuilder.asc(root.get(column.getFieldName())));
                    break;
                case DESC:
                    orderList.add(criteriaBuilder.desc(root.get(column.getFieldName())));
                    break;
            }
        }
        criteriaQuery.orderBy(orderList);
        return this;
    }


}




