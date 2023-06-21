package org.kibo;

import java.util.ArrayList;
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

    private List<Predicate> andPredicates = new ArrayList<>();

    private List<Predicate> orPredicates = new ArrayList<>();

    private Root<T> root;

    public UpdateQueryFactory(
        EntityManager em,
        Class<T> entityClass
    ) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.updateCq = cb.createCriteriaUpdate(entityClass);
        this.root = updateCq.from(entityClass);
    }

    public UpdateQueryFactory<T> set(
        Column column,
        Object value
    ) {
        this.updateCq.set(root.get(column.getFieldName()), value);
        return this;
    }

    public UpdateQueryFactory<T> where(WhereCondition... condition) {
        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );
        return this;
    }

    public UpdateQueryFactory<T> whereAnd(WhereCondition... condition) {

        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition))
            ));
        return this;
    }

    public UpdateQueryFactory<T> whereOr(WhereCondition... condition) {

        orPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );

        return this;
    }

    public int execute() {

        updateCq.where(
            andPredicates.isEmpty() ? cb.conjunction()
                : cb.and(andPredicates.toArray(new Predicate[0])),
            orPredicates.isEmpty() ? cb.conjunction()
                : cb.or(orPredicates.toArray(new Predicate[0]))
        );

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

