package org.kibo;

import org.kibo.where.WhereCondition;

import java.util.Arrays;
import java.util.List;

public class OnConditions {
    private final List<WhereCondition> conditions;

    public OnConditions(List<WhereCondition> conditions) {
        this.conditions = conditions;
    }

    public static OnConditions on(WhereCondition... conditions) {
        return new OnConditions(Arrays.asList(conditions));
    }

    public List<WhereCondition> getConditions() {
        return this.conditions;
    }

}
