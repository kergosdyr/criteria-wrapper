package org.kibo.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kibo.Column.col;

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
import org.kibo.OnConditions;
import org.kibo.QueryFactoryBuilder;
import org.kibo.entity.User;
import org.kibo.entity.UserOrder;
import org.kibo.where.ConditionBuilder;

public class QueryFactoryJoinTest {

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

    // 테스트 1: UserOrder와 User를 조인하여, 사용자의 상태가 "active"인 주문만 조회
    @Test
    public void testJoinOnUserActiveStatus() {
        User activeUser = new User();
        activeUser.setName("ActiveUser");
        activeUser.setAge(30);
        activeUser.setStatus("active");

        User inactiveUser = new User();
        inactiveUser.setName("InactiveUser");
        inactiveUser.setAge(40);
        inactiveUser.setStatus("inactive");

        em.persist(activeUser);
        em.persist(inactiveUser);

        UserOrder order1 = new UserOrder();
        order1.setOrderNumber("ORDER1");
        order1.setUser(activeUser);

        UserOrder order2 = new UserOrder();
        order2.setOrderNumber("ORDER2");
        order2.setUser(inactiveUser);

        em.persist(order1);
        em.persist(order2);
        em.flush();
        em.clear();

        List<UserOrder> orders = QueryFactoryBuilder.create(em)
            .select(UserOrder.class)
            // "user" 관계에 대해 조인하고, on 조건으로 조인된 User의 status가 "active"인지 검사
            .join(col("user"), OnConditions.on(col("status").eq("active")))
            .build();

        assertEquals(1, orders.size());
        assertEquals("ORDER1", orders.get(0).getOrderNumber());
    }

    // 테스트 2: 복합 조인 조건 (사용자 상태가 'active'이고 나이가 25보다 크거나, 이름이 'SpecialUser'인 경우)
    @Test
    public void testComplexJoinOnUser() {
        User user1 = new User();
        user1.setName("SpecialUser");
        user1.setAge(22);
        user1.setStatus("inactive");

        User user2 = new User();
        user2.setName("RegularUser");
        user2.setAge(30);
        user2.setStatus("active");

        User user3 = new User();
        user3.setName("AnotherUser");
        user3.setAge(40);
        user3.setStatus("active");

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        UserOrder order1 = new UserOrder();
        order1.setOrderNumber("ORDER_SPECIAL");
        order1.setUser(user1);

        UserOrder order2 = new UserOrder();
        order2.setOrderNumber("ORDER_REGULAR");
        order2.setUser(user2);

        UserOrder order3 = new UserOrder();
        order3.setOrderNumber("ORDER_ANOTHER");
        order3.setUser(user3);

        em.persist(order1);
        em.persist(order2);
        em.persist(order3);
        em.flush();
        em.clear();

        // 조인 on 조건: (status = 'active' AND age > 25) OR (name = 'SpecialUser')
        List<UserOrder> orders = QueryFactoryBuilder.create(em)
            .select(UserOrder.class)
            .join(col("user"), OnConditions.on(
                ConditionBuilder.start()
                    .and(col("status").eq("active"))
                    .and(col("age").gt(25))
                    .or(col("name").eq("SpecialUser"))
            ))
            .build();

        // 각 사용자는 아래 조건을 만족:
        // - user1: 이름이 'SpecialUser' 이므로 조건에 부합
        // - user2: 상태가 active이고 나이가 30 (>25)이므로 조건에 부합
        // - user3: 상태가 active이고 나이가 40 (>25)이므로 조건에 부합
        assertEquals(3, orders.size());
    }
}
