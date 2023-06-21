package org.kibo;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.kibo.where.WhereCondition;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Column implements Selector {

    private final Class entityClass;
    private final String fieldName;


    private Column(
            Class entityClass,
            String fieldName
    ) {
        this.entityClass = entityClass;
        this.fieldName = fieldName;
    }

    public static Column col(
            Class entityClass,
            String fieldName
    ) {
        return new Column(entityClass, fieldName);
    }

    public static Column col(
            String fieldName
    ) {
        return new Column(null, fieldName);
    }

    public WhereCondition eq(Object value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.equal(
                root.get(fieldName), value);
    }

    public OnCondition eq(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {


            Path rootPath = getRootPath(column, root, criteriaQuery);
            Path joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.equal(rootPath, joinRootPath);
        };
    }

    public WhereCondition ne(Object value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.notEqual(
                root.get(fieldName), value);
    }

    public WhereCondition ne(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Path rootPath = getRootPath(column, root, criteriaQuery);
            Path joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.notEqual(rootPath, joinRootPath);
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

            Path<? extends Comparable> rootPath = getRootPath(column, root, criteriaQuery);
            Path<? extends Comparable> joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.greaterThan(rootPath, joinRootPath);
        };
    }

    public WhereCondition lt(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThan(
                root.get(fieldName), value);
    }

    public WhereCondition lt(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Path<? extends Comparable> rootPath = getRootPath(column, root, criteriaQuery);
            Path<? extends Comparable> joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.lessThan(rootPath, joinRootPath);
        };
    }

    public WhereCondition gte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.greaterThanOrEqualTo(
                root.get(fieldName), value);
    }

    public WhereCondition gte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Path<? extends Comparable> rootPath = getRootPath(column, root, criteriaQuery);
            Path<? extends Comparable> joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.greaterThanOrEqualTo(rootPath, joinRootPath);
        };
    }

    public WhereCondition lte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThanOrEqualTo(
                root.get(fieldName), value);
    }

    public WhereCondition lte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Path<? extends Comparable> rootPath = getRootPath(column, root, criteriaQuery);
            Path<? extends Comparable> joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return criteriaBuilder.lessThanOrEqualTo(rootPath, joinRootPath);
        };
    }

    public WhereCondition between(
            Comparable value1,
            Comparable value2
    ) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.between(
                root.get(fieldName), value1, value2);
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

            Path rootPath = getRootPath(column, root, criteriaQuery);
            Path joinRootPath = getJoinRootPath(column, root, criteriaQuery);

            return rootPath.in(joinRootPath);
        };
    }

    private boolean isColumnTypeNotSameAsRoot(From<?, ?> root) {
        return this.entityClass != null && this.getEntityClass() != root.getJavaType();
    }
    private boolean isThisColumnTypeSameAs(From<?, ?> root) {
        return this.entityClass == null || this.getEntityClass() == root.getJavaType();
    }


    private Path<? extends Comparable> getRootPath(Column column, From<?, ?> root, CriteriaQuery criteriaQuery) {
        if (isThisColumnTypeSameAs(root)) {
            return root.get(this.fieldName);
        } else {
            return root.get(column.getFieldName());
        }
    }

    private Path<? extends Comparable> getJoinRootPath(Column column, From<?, ?> root, CriteriaQuery criteriaQuery) {

        if (isThisColumnTypeSameAs(root)) {
            Root joinRoot = criteriaQuery.from(column.getEntityClass());
            return joinRoot.get(column.getFieldName());
        } else {
            Root joinRoot = criteriaQuery.from(this.entityClass);
            return joinRoot.get(this.fieldName);
        }

    }


    public String getFieldName() {
        return this.fieldName;
    }

    public Class getEntityClass() {
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
