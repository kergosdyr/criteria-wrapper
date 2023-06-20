package org.kibo.aggregation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.kibo.Column;
import org.kibo.Selector;

public class SumAggregation implements Aggregation {

    private final Column column;

    private SumAggregation(Column column) {
        this.column = column;
    }

    public static SumAggregation sum(Column column) {
        return new SumAggregation(column);
    }


    @Override
    public Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    ) {
        return cb.sum(root.get(this.column.getFieldName()));
    }
}
