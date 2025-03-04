package org.kibo.where;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;

public class OrConditions implements WhereCondition {
    private final List<WhereCondition> conditions;

    public OrConditions(WhereCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public static WhereCondition or(WhereCondition... conditions) {
        return new OrConditions(conditions);
    }


    @Override
    public Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery) {
        return criteriaBuilder.or(
                conditions.stream()
                        .map(condition -> condition.toPredicate(root, criteriaBuilder, criteriaQuery))
                        .toArray(Predicate[]::new)
        );
    }
}
