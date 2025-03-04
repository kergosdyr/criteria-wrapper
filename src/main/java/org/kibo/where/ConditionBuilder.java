package org.kibo.where;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class ConditionBuilder implements WhereCondition {

    // tokens에는 WhereCondition과 "AND"/"OR" 문자열을 순서대로 저장합니다.
    private final List<Object> tokens = new ArrayList<>();

    public static ConditionBuilder start() {
        return new ConditionBuilder();
    }

    public ConditionBuilder and(WhereCondition condition) {
        // 첫 토큰이 아니라면 연산자 추가
        if (!tokens.isEmpty()) {
            tokens.add("AND");
        }
        tokens.add(condition);
        return this;
    }

    public ConditionBuilder or(WhereCondition condition) {
        // 첫 토큰이 아니라면 연산자 추가
        if (!tokens.isEmpty()) {
            tokens.add("OR");
        }
        tokens.add(condition);
        return this;
    }

    @Override
    public Predicate toPredicate(From<?, ?> root, CriteriaBuilder cb, CriteriaQuery<?> cq) {
        if (tokens.isEmpty()) {
            return cb.conjunction();
        }
        // AND 조건끼리 묶은 후, OR로 결합하는 식으로 우선순위를 반영합니다.
        List<Predicate> orGroups = new ArrayList<>();
        List<Predicate> currentAndGroup = new ArrayList<>();

        int i = 0;
        while (i < tokens.size()) {
            Object token = tokens.get(i);
            if (token instanceof WhereCondition) {
                // 조건 토큰이면 현재 그룹에 추가
                Predicate pred = ((WhereCondition) token).toPredicate(root, cb, cq);
                currentAndGroup.add(pred);
                i++;
            } else if (token instanceof String) {
                String op = (String) token;
                i++;
                if ("OR".equalsIgnoreCase(op)) {
                    // OR를 만나면 현재까지의 AND 그룹을 하나로 묶고, OR 그룹에 추가
                    if (!currentAndGroup.isEmpty()) {
                        Predicate andGroup = currentAndGroup.size() == 1
                            ? currentAndGroup.get(0)
                            : cb.and(currentAndGroup.toArray(new Predicate[0]));
                        orGroups.add(andGroup);
                        currentAndGroup = new ArrayList<>();
                    }
                    // 이후 조건은 새로운 AND 그룹의 시작
                }
                // 만약 "AND"이면 아무런 처리 없이 다음 조건이 같은 그룹에 추가됩니다.
            }
        }
        // 마지막에 남은 조건 그룹 처리
        if (!currentAndGroup.isEmpty()) {
            Predicate andGroup = currentAndGroup.size() == 1
                ? currentAndGroup.get(0)
                : cb.and(currentAndGroup.toArray(new Predicate[0]));
            orGroups.add(andGroup);
        }
        // 만약 OR 그룹이 하나뿐이면 그대로 반환, 둘 이상이면 OR로 결합
        return orGroups.size() == 1
            ? orGroups.get(0)
            : cb.or(orGroups.toArray(new Predicate[0]));
    }
}
