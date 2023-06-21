package org.kibo;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.kibo.where.WhereCondition;


public class DeleteQueryFactory<T> {
    private final EntityManager em;
    private final CriteriaBuilder cb;
    private CriteriaDelete<T> deleteCq;
    private Root<T> root;

    public DeleteQueryFactory(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.deleteCq = cb.createCriteriaDelete(entityClass);
        this.root = deleteCq.from(entityClass);

    }

    public DeleteQueryFactory<T> where(WhereCondition... condition) {
        deleteCq.where(
            cb.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public DeleteQueryFactory<T> whereAnd(WhereCondition... condition) {
        deleteCq.where(
            cb.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public DeleteQueryFactory<T> whereOr(WhereCondition... condition) {
        deleteCq.where(
            cb.or(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    private Predicate[] getPredicateFromCondition(
        From<?, ?> from,
        List<WhereCondition> conditions
    ) {
        return conditions.stream().map(condition -> condition != null
            ? condition.toPredicate(from, cb, deleteCq)
            : cb.conjunction()).toArray(Predicate[]::new
        );
    }


    public int execute() {
        return this.em.createQuery(this.deleteCq).executeUpdate();
    }
}