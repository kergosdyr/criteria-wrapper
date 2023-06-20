package org.kibo.aggregation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.kibo.Column;
import org.kibo.Selector;

public class MaxAggregation implements Aggregation {

    private final Column column;

    private MaxAggregation(Column column) {
        this.column = column;
    }

    public static MaxAggregation max(Column column) {
        return new MaxAggregation(column);
    }


    @Override
    public Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    ) {
        return cb.max(root.get(this.column.getFieldName()));
    }
}
