package org.kibo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public interface Selector {

    Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    );

}
