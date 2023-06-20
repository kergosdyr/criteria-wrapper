package org.kibo;

import javax.persistence.EntityManager;

public class DeleteQueryFactory<T> extends QueryFactory<T> {

    DeleteQueryFactory(
        EntityManager em,
        Class<T> clazz
    ) {
        super(em, clazz);
    }
}
