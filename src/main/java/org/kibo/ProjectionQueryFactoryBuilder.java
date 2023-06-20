package org.kibo;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

public class ProjectionQueryFactoryBuilder {

    private final EntityManager em;

    private List<Selector> selectors;

    ProjectionQueryFactoryBuilder(
        EntityManager em,
        List<Selector> selectors
    ) {
        this.em = em;
        this.selectors = selectors;
    }

    public <T> ProjectionQueryFactory<T> as(Class<T> projectionClass) {
        return new ProjectionQueryFactory<>(em, projectionClass, selectors);
    }
}
