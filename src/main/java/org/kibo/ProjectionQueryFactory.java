package org.kibo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

public class ProjectionQueryFactory<T> {

    private final EntityManager em;

    private List<Selector> selectors;


    private Class<T> projectionClass;

    public ProjectionQueryFactory(
        EntityManager em,
        Selector... selectors
    ) {
        this.em = em;
        this.selectors = Arrays.asList(selectors);
    }

    ProjectionQueryFactory(
        EntityManager em,
        Class<T> projectionClass,
        List<Selector> selectors
    ) {
        this.em = em;
        this.projectionClass = projectionClass;
        this.selectors = selectors;

    }

    public <R> QueryFactory<T> from(Class<R> clazz) {
        QueryFactory<R> queryFactory = new QueryFactory<>(
            this.em, clazz, projectionClass
        );

        return queryFactory.selectMulti(this.projectionClass, this.selectors);
    }
}
