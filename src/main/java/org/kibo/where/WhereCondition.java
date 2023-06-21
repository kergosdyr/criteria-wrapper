package org.kibo.where;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface WhereCondition {

    Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder, CommonAbstractCriteria criteriaQuery);

}
