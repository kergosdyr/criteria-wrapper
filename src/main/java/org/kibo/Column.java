package org.kibo;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.kibo.where.WhereCondition;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Column implements Selector {

    private Class entityClass;
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


            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.equal(result.rootPath, result.joinRootPath);
        };
    }

    private Result getRootPathResult(
        Column column,
        From<?, ?> root,
        CriteriaQuery criteriaQuery
    ) {
        Path<? extends Comparable> rootPath;
        Path<? extends Comparable> joinRootPath;

        if (!isColumnTypeSameAsRoot(this, root)) {
            Root joinRoot = criteriaQuery.from(this.getEntityClass());
            rootPath = root.get(column.getFieldName());
            joinRootPath = joinRoot.get(this.fieldName);
        } else {
            Root joinRoot = criteriaQuery.from(column.getEntityClass());
            rootPath = root.get(this.fieldName);
            joinRootPath = joinRoot.get(column.getFieldName());
        }
        return new Result(rootPath, joinRootPath);
    }

    private static class Result {

        private final Path<? extends Comparable> rootPath;
        private final Path<? extends Comparable> joinRootPath;

        public Result(
            Path<? extends Comparable> rootPath,
            Path<? extends Comparable> joinRootPath
        ) {
            this.rootPath = rootPath;
            this.joinRootPath = joinRootPath;
        }


    }

    private static boolean isColumnTypeSameAsRoot(
        Column column,
        From<?, ?> root
    ) {
        return column.entityClass == null || column.getEntityClass() == root.getJavaType();
    }

    public WhereCondition ne(Object value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.notEqual(
            root.get(fieldName), value);
    }

    public WhereCondition ne(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.notEqual(result.rootPath, result.joinRootPath);
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

            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.greaterThan(result.rootPath, result.joinRootPath);
        };
    }

    public WhereCondition lt(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThan(
            root.get(fieldName), value);
    }

    public WhereCondition lt(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.lessThan(result.rootPath, result.joinRootPath);
        };
    }

    public WhereCondition gte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.greaterThanOrEqualTo(
            root.get(fieldName), value);
    }

    public WhereCondition gte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.greaterThanOrEqualTo(result.rootPath, result.joinRootPath);
        };
    }

    public WhereCondition lte(Comparable value) {
        return (root, criteriaBuilder, criteriaQuery) -> criteriaBuilder.lessThanOrEqualTo(
            root.get(fieldName), value);
    }

    public WhereCondition lte(Column column) {
        return (root, criteriaBuilder, criteriaQuery) -> {

            Result result = getRootPathResult(column, root, criteriaQuery);

            return criteriaBuilder.lessThanOrEqualTo(result.rootPath, result.joinRootPath);
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

            Result result = getRootPathResult(column, root, criteriaQuery);

            return result.rootPath.in(result.joinRootPath);
        };
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
