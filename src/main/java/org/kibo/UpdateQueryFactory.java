package org.kibo;

import javax.persistence.EntityManager;

public class UpdateQueryFactory<T> extends QueryFactory<T> {


    UpdateQueryFactory(
        EntityManager em,
        Class<T> clazz
    ) {
        super(em, clazz);
    }
}
