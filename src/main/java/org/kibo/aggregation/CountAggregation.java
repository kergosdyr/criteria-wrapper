package org.kibo.aggregation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.kibo.Column;

public class CountAggregation implements Aggregation {

    private final Column column;

    private CountAggregation(Column column) {
        this.column = column;
    }

    public static CountAggregation count(Column column) {
        return new CountAggregation(column);
    }


    @Override
    public Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    ) {
        return cb.count(root.get(this.column.getFieldName()));
    }
}
