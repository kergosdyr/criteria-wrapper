package org.kibo;

import java.util.ArrayList;
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

    private List<Predicate> andPredicates = new ArrayList<>();

    private List<Predicate> orPredicates = new ArrayList<>();


    public DeleteQueryFactory(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.deleteCq = cb.createCriteriaDelete(entityClass);
        this.root = deleteCq.from(entityClass);

    }

    public DeleteQueryFactory<T> where(WhereCondition... condition) {
        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );
        return this;
    }

    public DeleteQueryFactory<T> whereAnd(WhereCondition... condition) {

        andPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition))
            ));
        return this;
    }

    public DeleteQueryFactory<T> whereOr(WhereCondition... condition) {

        orPredicates.addAll(
            Arrays.asList(getPredicateFromCondition(root, Arrays.asList(condition)))
        );

        return this;
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


    public int execute() {
        deleteCq.where(
            andPredicates.isEmpty() ? cb.conjunction()
                : cb.and(andPredicates.toArray(new Predicate[0])),
            orPredicates.isEmpty() ? cb.conjunction()
                : cb.or(orPredicates.toArray(new Predicate[0]))
        );

        return this.em.createQuery(this.deleteCq).executeUpdate();
    }
}