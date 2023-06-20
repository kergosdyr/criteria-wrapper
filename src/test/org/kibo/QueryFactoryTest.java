package org.kibo;

import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;


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




}