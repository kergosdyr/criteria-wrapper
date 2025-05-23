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
import org.kibo.where.WhereCondition;

@SuppressWarnings({"rawtypes", "unchecked"})
public class QueryFactory<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder cb;
    private CriteriaQuery cq;
    private final Root<T> root;

    private final List<Predicate> andPredicates = new ArrayList<>();

    private final List<Predicate> orPredicates = new ArrayList<>();

    private final List<Predicate> innerJoinPredicate = new ArrayList<>();

    private final List<Predicate> crossJoinPredicate = new ArrayList<>();

    private int offset = 0;
    private int limit = Integer.MAX_VALUE;

    public QueryFactory(
        EntityManager em,
        Class<T> clazz
    ) {
        this.entityManager = em;
        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery(clazz);
        this.root = cq.from(clazz);
    }

    <R> QueryFactory(
        EntityManager em,
        CriteriaBuilder cb,
        CriteriaQuery cq,
        Root root,
        Class<T> clazz
    ) {
        this.entityManager = em;
        this.cb = cb;
        this.cq = cq;
        this.root = root;
    }

    <R> QueryFactory(
        EntityManager em,
        Class<T> clazz,
        Class<R> projectionClass
    ) {
        this.entityManager = em;
        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery(projectionClass);
        this.root = cq.from(clazz);
    }

    public static QueryFactoryBuilder db(EntityManager entityManager) {
        return new QueryFactoryBuilder(entityManager);
    }

    private QueryFactory(
        QueryFactory<T> queryFactory,
        Root<T> root
    ) {
        this.entityManager = queryFactory.entityManager;
        this.cb = queryFactory.cb;
        this.cq = queryFactory.cq;
        this.root = root;
    }

    public QueryFactory<T> select(Class<T> clazz) {
        cq = cb.createQuery(clazz);
        cq.select(cq.from(clazz));
        return this;
    }

    <R> QueryFactory<R> selectMulti(
        Class<R> projectionClass,
        List<Selector> selectors
    ) {

        cq = cq.select(cb.construct(
            projectionClass,
            selectors.stream()
                .map(selector -> selector.toSelection(this.cb, this.root))
                .toArray(Selection[]::new)
        ));

        return new QueryFactory<R>(
            this.entityManager, this.cb, this.cq, this.root,
            projectionClass
        );


    }

    public QueryFactory<T> from(Class<T> clazz) {
        return this;
    }

    public QueryFactory<T> where(WhereCondition... condition) {
        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );
        return this;
    }

    public QueryFactory<T> whereAnd(WhereCondition... condition) {

        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition))
            ));
        return this;
    }

    public QueryFactory<T> whereOr(WhereCondition... condition) {

        orPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );

        return this;
    }

    public QueryFactory<T> join(Column column, OnConditions onConditions) {

        Join<Object, Object> join = root.join(column.getFieldName(), JoinType.INNER);

        if (onConditions != null && !onConditions.getConditions().isEmpty()) {
            Predicate[] predicates = getPredicateFromCondition(join, onConditions.getConditions());
            join.on(predicates);
        }
        return this;
    }

    private boolean isThisColumnSameAsRoot(Column column) {
        return column.getEntityClass() == null || column.getEntityClass() == root.getJavaType();
    }

    private Predicate[] getPredicateFromCondition(
        From<?, ?> from,
        List<WhereCondition> conditions
    ) {
        return conditions.stream().map(condition -> condition != null
            ? condition.toPredicate(from, cb, cq)
            : cb.conjunction()).toArray(Predicate[]::new
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

    // left join 메서드 추가
    public QueryFactory<T> leftJoin(Column column, OnConditions onConditions) {
        Join<Object, Object> join = root.join(column.getFieldName(), JoinType.LEFT);
        if (onConditions != null && !onConditions.getConditions().isEmpty()) {
            Predicate[] predicates = getPredicateFromCondition(join, onConditions.getConditions());
            join.on(predicates);
        }
        return this;
    }

    // on 조건 없이 단순 left join
    public QueryFactory<T> leftJoin(Column column) {
        root.join(column.getFieldName(), JoinType.LEFT);
        return this;
    }


    public <K> QueryFactory<T> join(
        Class<K> joinClass,
        OnConditions onConditions
    ) {

        crossJoinPredicate.addAll(
            onConditions.getConditions().stream()
                .map(condtion -> condtion.toPredicate(root, cb, cq))
                .collect(Collectors.toList())
        );

        return this;
    }

    public QueryFactory<T> having(WhereCondition condition) {
        cq.having(condition.toPredicate(root, cb, cq));
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

        cq.where(
            innerJoinPredicate.isEmpty() ? cb.conjunction()
                : cb.and(innerJoinPredicate.toArray(new Predicate[0])),
            crossJoinPredicate.isEmpty() ? cb.conjunction()
                : cb.and(crossJoinPredicate.toArray(new Predicate[0])),
            andPredicates.isEmpty() ? cb.conjunction()
                : cb.and(andPredicates.toArray(new Predicate[0])),
            orPredicates.isEmpty() ? cb.conjunction()
                : cb.or(orPredicates.toArray(new Predicate[0]))
        );

        return entityManager
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    public List<T> build() {

        cq.where(
            innerJoinPredicate.isEmpty() ? cb.conjunction()
                : cb.and(innerJoinPredicate.toArray(new Predicate[0])),
            crossJoinPredicate.isEmpty() ? cb.conjunction()
                : cb.and(crossJoinPredicate.toArray(new Predicate[0])),
            andPredicates.isEmpty() ? cb.conjunction()
                : cb.and(andPredicates.toArray(new Predicate[0])),
            orPredicates.isEmpty() ? cb.conjunction()
                : cb.or(orPredicates.toArray(new Predicate[0]))
        );

        return entityManager
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }


    public Optional<T> getResult() {
        return this.getResultList().stream().findFirst();
    }

    public QueryFactory<T> groupBy(Column... columns) {
        List<Expression<?>> groupByList = new ArrayList<>();
        for (Column column : columns) {
            groupByList.add(root.get(column.getFieldName()));
        }
        cq.groupBy(groupByList);
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
                    orderList.add(cb.asc(root.get(column.getFieldName())));
                    break;
                case DESC:
                    orderList.add(cb.desc(root.get(column.getFieldName())));
                    break;
            }
        }
        cq.orderBy(orderList);
        return this;
    }


}




