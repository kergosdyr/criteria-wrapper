package org.kibo.where;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;

public class AndConditions implements WhereCondition {
    private final List<WhereCondition> conditions;

    public AndConditions(WhereCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }
    public static WhereCondition and(WhereCondition... conditions) {
        return new AndConditions(conditions);
    }


    @Override
    public Predicate toPredicate(From<?, ?> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                conditions.stream()
                        .map(condition -> condition.toPredicate(root, criteriaBuilder))
                        .toArray(Predicate[]::new)
        );
    }
}

