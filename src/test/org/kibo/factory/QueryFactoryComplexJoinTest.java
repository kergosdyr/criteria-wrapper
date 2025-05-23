package org.kibo.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.kibo.entity.Product;
import org.kibo.entity.User;
import org.kibo.entity.UserDetail;
import org.kibo.entity.UserOrder;
import org.kibo.where.ConditionBuilder;

public class QueryFactoryComplexJoinTest {

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
    
    // 3중 조인 테스트: UserOrder → User → UserDetail
    @Test
    public void testTripleJoinWithUserAndUserDetail() {
        // User 생성
        User user = new User();
        user.setName("TripleJoinUser");
        user.setAge(35);
        user.setStatus("active");
        em.persist(user);
        
        // UserOrder 생성 (User와 연관)
        UserOrder order = new UserOrder();
        order.setOrderNumber("ORDER_TRIPLE");
        order.setUser(user);
        em.persist(order);
        
        // UserDetail 생성 (User와 1:1)
        UserDetail detail = new UserDetail();
        detail.setEmail("user@example.com");
        detail.setUser(user);
        em.persist(detail);
        
        em.flush();
        em.clear();
        
        // 쿼리: UserOrder를 조회하면서,
        // 1) inner join으로 연관된 User의 상태가 "active"인지 확인하고,
        // 2) cross join 방식으로 UserDetail과 조인하여 UserDetail의 email 조건도 적용
        List<UserOrder> results = QueryFactoryBuilder.create(em)
            .select(UserOrder.class)
            .join(col("user"), OnConditions.on(col("status").eq("active")))
            .join(UserDetail.class, OnConditions.on(
                // UserDetail.user와 User.id를 비교
                col(UserDetail.class, "user").eq(col(User.class, "id")),
                // 추가 조건: email에 "example.com" 포함
                col(UserDetail.class, "email").like("%example.com")
            ))
            .build();
        
        assertEquals(1, results.size());
        assertEquals("ORDER_TRIPLE", results.get(0).getOrderNumber());
    }
    
    // Left Outer Join 테스트: Product와 UserOrder 간의 outer join
    // 주의: 아래 코드는 QueryFactory에 leftJoin 메서드가 구현되어 있다고 가정합니다.
    @Test
    public void testLeftOuterJoinOnProductAndOrder() {
        // User 생성
        User user = new User();
        user.setName("OuterJoinUser");
        user.setAge(28);
        user.setStatus("active");
        em.persist(user);

        // UserOrder 생성 (User와 연관)
        UserOrder order = new UserOrder();
        order.setOrderNumber("ORDER_LEFT");
        order.setUser(user);
        em.persist(order);

        // Product 생성 (연관된 UserOrder가 있는 경우)
        Product productWithOrder = new Product();
        productWithOrder.setProductName("Laptop");
        productWithOrder.setUserOrder(order);
        em.persist(productWithOrder);

        // Product 생성 (연관된 UserOrder가 없는 경우)
        Product productWithoutOrder = new Product();
        productWithoutOrder.setProductName("Tablet");
        // userOrder 필드는 null
        em.persist(productWithoutOrder);

        em.flush();
        em.clear();

        // 쿼리: Product를 대상으로 left outer join을 수행하여,
        // 연관된 UserOrder의 orderNumber가 "ORDER%"인 경우를 조건으로 지정
        // (leftJoin 메서드는 inner join과 달리 연관 데이터가 없어도 Product를 반환해야 합니다.)
        List<Product> products = QueryFactoryBuilder.create(em)
            .select(Product.class)
            .leftJoin(col("userOrder"), OnConditions.on(
                col("orderNumber").like("ORDER%")
            ))
            .build();

        // 두 개의 Product 모두 조회되어야 함 (연관 주문이 없더라도 포함)
        assertEquals(2, products.size());
    }

    @Test
    public void testLeftJoin_AssociationAbsent_ShouldBeNull() {
        // 1) 엔티티 생성 및 저장
        User user = new User();
        user.setName("TestUser");
        user.setAge(20);
        user.setStatus("active");
        em.persist(user);

        UserOrder order = new UserOrder();
        order.setOrderNumber("ORDER_X");
        order.setUser(user);
        em.persist(order);

        // 연관 주문이 있는 상품
        Product withOrder = new Product();
        withOrder.setProductName("WithOrder");
        withOrder.setUserOrder(order);
        em.persist(withOrder);

        // 연관 주문이 없는 상품
        Product withoutOrder = new Product();
        withoutOrder.setProductName("WithoutOrder");
        // userOrder 필드를 설정하지 않음 → null
        em.persist(withoutOrder);

        em.flush();
        em.clear();

        // 2) LEFT JOIN 수행 (orderNumber LIKE 'ORDER%')
        List<Product> products = QueryFactoryBuilder.create(em)
            .select(Product.class)
            .leftJoin(col("userOrder"), OnConditions.on(
                col("orderNumber").like("ORDER%")
            ))
            .build();

        // 상품이 두 건 조회되어야 함
        assertEquals(2, products.size());

        // 3) “WithoutOrder” 상품을 찾아 userOrder가 null인지 확인
        Product fetchedNo = products.stream()
            .filter(p -> "WithoutOrder".equals(p.getProductName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("WithoutOrder 상품이 조회되지 않음"));
        assertNull(fetchedNo.getUserOrder(), "연관 주문이 없으면 userOrder는 null이어야 합니다");

        // 4) “WithOrder” 상품은 정상 조인되어 orderNumber가 ORDER_X 여야 함
        Product fetchedYes = products.stream()
            .filter(p -> "WithOrder".equals(p.getProductName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("WithOrder 상품이 조회되지 않음"));
        assertNotNull(fetchedYes.getUserOrder(), "연관 주문이 있으면 userOrder가 null이 아니어야 합니다");
        assertEquals("ORDER_X", fetchedYes.getUserOrder().getOrderNumber());
    }

}
