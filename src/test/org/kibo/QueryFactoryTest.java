package org.kibo;

import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;
import org.kibo.QueryFactoryTest.TestDto.Fields;

import javax.persistence.EntityManager;

import static org.kibo.Column.col;
import static org.kibo.OnConditions.on;
import static org.kibo.QueryFactoryTest.TestDto.Fields.*;
import static org.kibo.QueryFactoryTest.TestDto.Fields.name;
import static org.kibo.where.AndConditions.and;


public class QueryFactoryTest {



    @FieldNameConstants
    public static class TestDto {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zip;
    }

    //TestTwoDTo
    @FieldNameConstants
    public static class TestTwoDto {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zip;
    }





    @Test

    void name() {
        EntityManager webServiceEm = null;

        QueryFactory
                .db(webServiceEm)
                .select(TestDto.class)
                .from(TestDto.class)
                .join(Column.col(name), on( col(TestTwoDto.Fields.name).eq("21") ))
                .where(
                        and(
                                col(name).gt(3),
                                col(name).eq(""),
                                col(email).eq(""),
                                col(phone).eq(""),
                                col(address).eq(""),
                                col(city).eq(""),
                                col(state).eq(""),
                                col(zip).eq("")
                        )
                ).getResultList();



    }
}