package org.kibo.where;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface WhereCondition {

    Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery);

    default WhereCondition and(WhereCondition other) {
        return (root, cb, cq) -> cb.and(this.toPredicate(root, cb, cq), other.toPredicate(root, cb, cq));
    }

    default WhereCondition or(WhereCondition other) {
        return (root, cb, cq) -> cb.or(this.toPredicate(root, cb, cq), other.toPredicate(root, cb, cq));
    }


}
