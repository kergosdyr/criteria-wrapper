package org.kibo.where;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface WhereCondition {

    Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder);

}
