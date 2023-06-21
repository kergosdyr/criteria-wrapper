package org.kibo;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.kibo.where.WhereCondition;

public class UpdateQueryFactory<T> {
    private final EntityManager em;
    private final CriteriaBuilder cb;
    private CriteriaUpdate<T> updateCq;
    private Root<T> root;

    public UpdateQueryFactory(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.updateCq = cb.createCriteriaUpdate(entityClass);
        this.root = updateCq.from(entityClass);
    }
    public UpdateQueryFactory<T> set(Column column, Object value) {
        this.updateCq.set(root.get(column.getFieldName()), value);
        return this;
    }

    public UpdateQueryFactory<T> where(WhereCondition... condition) {
        updateCq.where(
            cb.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public UpdateQueryFactory<T> whereAnd(WhereCondition... condition) {
        updateCq.where(
            cb.and(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public UpdateQueryFactory<T> whereOr(WhereCondition... condition) {
        updateCq.where(
            cb.or(getPredicateFromCondition(root, Arrays.asList(condition))));
        return this;
    }

    public int execute() {
        return this.em.createQuery(this.updateCq).executeUpdate();
    }

    private Predicate[] getPredicateFromCondition(
        From<?, ?> from,
        List<WhereCondition> conditions
    ) {
        return conditions.stream().map(condition -> condition != null
            ? condition.toPredicate(from, cb, null)
            : cb.conjunction()).toArray(Predicate[]::new
        );
    }

}

