package org.kibo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.kibo.where.WhereCondition;

public interface OnCondition extends WhereCondition {

    @SuppressWarnings("rawtypes")
    Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery);


}
