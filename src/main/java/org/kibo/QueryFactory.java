package org.kibo;

import org.kibo.where.WhereCondition;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QueryFactory<T> {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<T> criteriaQuery;
    private final Root<T> root;

    private int offset = 0;
    private int limit = Integer.MAX_VALUE;


    public QueryFactory(EntityManager em, Class<T> clazz) {
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(clazz);
        this.root = criteriaQuery.from(clazz);
    }

    public static QueryFactoryBuilder db(EntityManager entityManager) {
        return new QueryFactoryBuilder(entityManager);
    }


    private QueryFactory(QueryFactory<T> queryFactory, Root<T> root) {
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


    public QueryFactory<T> from(Class<T> clazz) {
        return this;
    }

    public QueryFactory<T> where(WhereCondition condition) {
        criteriaQuery.where(condition.toPredicate(root, criteriaBuilder));
        return this;
    }

    public QueryFactory<T> join(Column column, OnConditions onConditions) {
        Join<?, ?> join = root.join(column.getFieldName(), JoinType.INNER);
        criteriaQuery.where(criteriaBuilder.and(onConditions.getConditions().stream()
                .map(condition -> condition.toPredicate(join, criteriaBuilder))
                .toArray(Predicate[]::new)));
        return this;
    }

    public QueryFactory<T> join(Column column) {
        root.join(column.getFieldName(), JoinType.INNER);
        return this;
    }

    public QueryFactory<T> fetch(Column column) {
        root.fetch(column.getFieldName());
        return this;
    }

    public <K> QueryFactory<T> join(Class<K> joinClass, OnConditions onConditions) {
        criteriaQuery.from(joinClass);
        criteriaQuery.where(criteriaBuilder.and(onConditions.getConditions().stream()
                .map(condition -> condition.toPredicate(root, criteriaBuilder))
                .toArray(Predicate[]::new)));
        return this;
    }

    public QueryFactory<T> having(WhereCondition condition) {
        criteriaQuery.having(condition.toPredicate(root, criteriaBuilder));
        return this;
    }

    public QueryFactory<T> limit(int offset, int limit) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    public List<T> getResultList() {
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public Optional<T> getResult() {
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(offset)
                .getResultList().stream()
                .findFirst();
    }

    public QueryFactory<T> groupBy(Column... columns) {
        List<Expression<?>> groupByList = new ArrayList<>();
        for (Column column : columns) {
            groupByList.add(root.get(column.getFieldName()));
        }
        criteriaQuery.groupBy(groupByList);
        return this;
    }

    public QueryFactory<T> orderBy(OrderDirection direction, Column... columns) {
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


    public static class QueryFactoryBuilder {

        private final EntityManager em;

        public QueryFactoryBuilder(EntityManager entityManager) {
            this.em = entityManager;
        }

        public <T> QueryFactory<T> select(Class<T> clazz) {
            return new QueryFactory<>(this.em, clazz);
        }


    }


}




