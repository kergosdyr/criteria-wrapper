package org.kibo;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.kibo.where.WhereCondition;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Column<T> implements Selector {

    private final Class<T> entityClass;
    private final String fieldName;


    private Column(
        Class<T> entityClass,
        String fieldName
    ) {
        this.entityClass = entityClass;
        this.fieldName = fieldName;
    }

    public static <T> Column col(
        Class<T> entityClass,
        String fieldName
    ) {
        return new Column(entityClass, fieldName);
    }

    public WhereCondition eq(Object value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.equal(
            root.get(fieldName), value);
    }

    public OnCondition eq(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);

            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.equal(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.equal(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.equal(rootPath, joinRootPath);
        };
    }

    public WhereCondition ne(Object value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.notEqual(
            root.get(fieldName), value);
    }

    public WhereCondition ne(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);

            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.notEqual(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.notEqual(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.notEqual(
                root.get(fieldName), crossJoinRoot.get(column.getFieldName()));
        };
    }

    public WhereCondition like(String value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.like(
            root.get(fieldName), value);
    }

    public WhereCondition gt(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.greaterThan(
            root.get(fieldName), value);
    }

    public WhereCondition gt(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);
            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.greaterThan(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.greaterThan(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.greaterThan(
                root.get(fieldName), crossJoinRoot.get(column.getFieldName()));
        };
    }

    public WhereCondition lt(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThan(
            root.get(fieldName), value);
    }

    public WhereCondition lt(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);
            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.lessThan(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.lessThan(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.lessThan(
                root.get(fieldName), crossJoinRoot.get(column.getFieldName()));
        };
    }

    public WhereCondition gte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.greaterThanOrEqualTo(
            root.get(fieldName), value);
    }

    public WhereCondition gte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);

            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                root.get(fieldName), crossJoinRoot.get(column.getFieldName()));
        };
    }

    public WhereCondition lte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThanOrEqualTo(
            root.get(fieldName), value);
    }

    public WhereCondition lte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);

            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return criteriaBuilder.lessThanOrEqualTo(
                    root.get(this.fieldName), crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return criteriaBuilder.lessThanOrEqualTo(
                    root.get(column.getFieldName()), crossJoinRoot.get(this.fieldName));
            }

            return criteriaBuilder.lessThanOrEqualTo(
                root.get(fieldName), crossJoinRoot.get(column.getFieldName()));
        };
    }

    public WhereCondition between(
        Comparable value1,
        Comparable value2
    ) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.between(
            root.get(fieldName), value1, value2);
    }

    public WhereCondition between(
        Column column1,
        Column column2
    ) {
        return (root, criteriaBuilder, criteriaQuery) -> {
            Root rootFrom1 = criteriaQuery.from(column1.getEntityClass());
            Root rootFrom2 = criteriaQuery.from(column2.getEntityClass());
            return criteriaBuilder.between(
                root.get(fieldName), rootFrom1.get(column1.getFieldName()),
                rootFrom2.get(column2.getFieldName())
            );
        };
    }

    public WhereCondition isNotNull() {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.isNotNull(
            root.get(fieldName));
    }


    public WhereCondition isNull() {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.isNull(
            root.get(fieldName));
    }

    public WhereCondition in(Object... values) {
        return (root, criteriaBuilder, criteriaQuery) -> root.get(fieldName).in(values);
    }

    public WhereCondition in(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Root crossJoinRoot = criteriaQuery.from(column.getEntityClass());
            criteriaQuery.select(root);

            Path<Object> rootPath = root.get(this.fieldName);
            Path<Object> joinRootPath = crossJoinRoot.get(column.getFieldName());

            if (rootPath.getJavaType() == joinRootPath.getJavaType()) {
                return root.get(this.fieldName).in(crossJoinRoot.get(column.getFieldName()));
            }

            if (this.entityClass == crossJoinRoot.getJavaType()) {
                return root.get(column.getFieldName()).in(crossJoinRoot.get(this.fieldName));
            }

            return root.get(fieldName).in(crossJoinRoot.get(column.getFieldName()));
        };
    }


    public String getFieldName() {
        return this.fieldName;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public Expression<?> toSelection(
        CriteriaBuilder cb,
        Root<?> root
    ) {
        return root.get(this.fieldName);
    }
}
