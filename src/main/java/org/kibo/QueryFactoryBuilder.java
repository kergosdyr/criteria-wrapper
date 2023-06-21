package org.kibo;

import java.util.Arrays;
import javax.persistence.EntityManager;

public class QueryFactoryBuilder {

    private final EntityManager em;

    public QueryFactoryBuilder(EntityManager entityManager) {
        this.em = entityManager;
    }

    public <T> QueryFactory<T> select(Class<T> clazz) {
        return new QueryFactory<>(this.em, clazz);
    }
    public ProjectionQueryFactoryBuilder select(Selector... selectors) {
        return new ProjectionQueryFactoryBuilder(this.em, Arrays.asList(selectors));
    }
    public <T> UpdateQueryFactory<T> update(Class<T> clazz) {
        return new UpdateQueryFactory<>(this.em, clazz);
    }
    public <T> DeleteQueryFactory<T> delete(Class<T> clazz) {
        return new DeleteQueryFactory<>(this.em, clazz);
    }






}
