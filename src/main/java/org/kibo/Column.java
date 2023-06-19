package org.kibo;


import org.kibo.where.WhereCondition;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Column {
    private final String fieldName;

    private Column(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Column col(String name){
        return new Column(name);
    }

    public WhereCondition eq(Object value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(fieldName), value);
    }

    public WhereCondition ne(Object value) {
        return (root, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(fieldName), value);
    }

    public WhereCondition like(String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.like(root.get(fieldName), value);
    }
    public WhereCondition gt(Comparable value) {
        return (root, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(fieldName), value);
    }

    public WhereCondition lt(Comparable value) {
        return (root, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(fieldName), value);
    }

    public WhereCondition gte(Comparable value) {
        return (root, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), value);
    }

    public WhereCondition lte(Comparable value) {
        return (root, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), value);
    }

    public WhereCondition between(Comparable value1, Comparable value2) {
        return (root, criteriaBuilder) -> criteriaBuilder.between(root.get(fieldName), value1, value2);
    }

    public WhereCondition isNotNull() {
        return (root, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(fieldName));
    }

    public WhereCondition isNull() {
        return (root, criteriaBuilder) -> criteriaBuilder.isNull(root.get(fieldName));
    }

    public WhereCondition in(Object... values) {
        return (root, criteriaBuilder) -> root.get(fieldName).in(values);
    }


    public String getFieldName() {
        return this.fieldName;
    }
}
