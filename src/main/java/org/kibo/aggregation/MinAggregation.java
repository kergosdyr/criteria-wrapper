package org.kibo.aggregation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.kibo.Column;
import org.kibo.Selector;

public class MinAggregation implements Aggregation {

    private final Column column;

    private MinAggregation(Column column) {
        this.column = column;
    }

    public static MinAggregation min(Column column) {
        return new MinAggregation(column);
    }


    @Override
    public Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    ) {
        return cb.min(root.get(this.column.getFieldName()));
    }
}
