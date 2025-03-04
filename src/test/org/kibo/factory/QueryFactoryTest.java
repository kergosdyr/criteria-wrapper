package org.kibo.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kibo.Column.col;
import static org.kibo.entity.User.Fields.name;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kibo.QueryFactoryBuilder;
import org.kibo.aggregation.CountAggregation;
import org.kibo.entity.User;
import org.kibo.where.ConditionBuilder;

public class QueryFactoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeAll
    public static void setupAll() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }

    @AfterAll
    public static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    public void tearDown() {
        if (tx.isActive()) {
            tx.rollback();
        }
        if (em != null) {
            em.close();
        }
    }

    // 1. 단순 조회 테스트: 이름이 "John"인 사용자 조회
    @Test
    public void testInsertAndQuery() {
        User user1 = new User();
        user1.setName("John");
        user1.setAge(25);
        user1.setStatus("active");

        User user2 = new User();
        user2.setName("Jane");
        user2.setAge(30);
        user2.setStatus("inactive");

        em.persist(user1);
        em.persist(user2);
        em.flush();
        em.clear();

        List<User> result = QueryFactoryBuilder.create(em)
            .select(User.class)
            .where(col(name).eq("John"))
            .build();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    // 2. Aggregation 테스트: 이름이 "TestUser"인 사용자 수 집계
    @Test
    public void testAggregationCount() {
        User user1 = new User();
        user1.setName("TestUser");
        user1.setAge(20);
        user1.setStatus("active");

        User user2 = new User();
        user2.setName("TestUser");
        user2.setAge(30);
        user2.setStatus("active");

        em.persist(user1);
        em.persist(user2);
        em.flush();
        em.clear();

        // ProjectionQueryFactory를 활용하여 COUNT 집계 수행
        List<?> result = QueryFactoryBuilder.create(em)
            .select(CountAggregation.count(col("id")))
            .as(Long.class)
            .from(User.class)
            .where(col(name).eq("TestUser"))
            .build();

        // 집계 결과는 단일 값의 리스트로 반환됨 (예: Long 타입)
        Long count = (Long) result.get(0);
        assertEquals(2, count.intValue());
    }

    // 3. 복합 조건 테스트: (status = 'active' AND age > 30) OR (name = 'Alice')
    @Test
    public void testComplexWhereCondition() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setAge(28);
        user1.setStatus("active");

        User user2 = new User();
        user2.setName("Bob");
        user2.setAge(35);
        user2.setStatus("active");

        User user3 = new User();
        user3.setName("Charlie");
        user3.setAge(40);
        user3.setStatus("inactive");

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.flush();
        em.clear();

        // 조건: (status = 'active' AND age > 30) OR (name = 'Alice')
        List<User> result = QueryFactoryBuilder.create(em)
            .select(User.class)
            .where(
                ConditionBuilder.start()
                    .and(
                        ConditionBuilder.start()
                        .and(col("status").eq("active"))
                        .and(col("age").gt(30))
                    )
                    .or(col(name).eq("Alice"))
            )
            .build();

        // 조건에 맞는 사용자: 'Alice' (active, 28)와 'Bob' (active, 35)
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Alice")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Bob")));
    }

    // 4. 업데이트 테스트: 특정 조건에 맞는 사용자 상태 변경
    @Test
    public void testUpdateQuery() {
        User user = new User();
        user.setName("Mark");
        user.setAge(40);
        user.setStatus("inactive");

        em.persist(user);
        em.flush();
        em.clear();

        // status가 'inactive'인 사용자를 'active'로 업데이트
        int updated = QueryFactoryBuilder.create(em)
            .update(User.class)
            .where(col("status").eq("inactive"))
            .set(col("status"), "active")
            .execute();

        // 업데이트 성공 여부 검증
        assertEquals(1, updated);

        // 업데이트 후 데이터 확인
        List<User> result = QueryFactoryBuilder.create(em)
            .select(User.class)
            .where(col("name").eq("Mark"))
            .build();

        assertEquals(1, result.size());
        assertEquals("active", result.get(0).getStatus());
    }

    // 5. 삭제 테스트: 특정 조건에 맞는 사용자 삭제
    @Test
    public void testDeleteQuery() {
        User user = new User();
        user.setName("DeleteMe");
        user.setAge(50);
        user.setStatus("inactive");

        em.persist(user);
        em.flush();
        em.clear();

        // 이름이 'DeleteMe'인 사용자 삭제
        int deleted = QueryFactoryBuilder.create(em)
            .delete(User.class)
            .where(col("name").eq("DeleteMe"))
            .execute();

        // 삭제 성공 여부 검증
        assertEquals(1, deleted);

        // 삭제 후 데이터 확인: 조회 결과가 없어야 함
        List<User> result = QueryFactoryBuilder.create(em)
            .select(User.class)
            .where(col("name").eq("DeleteMe"))
            .build();

        assertEquals(0, result.size());
    }
}
