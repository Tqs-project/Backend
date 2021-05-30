package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;


@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private OrderRepository orderRepository;

    private Customer customer;
    private Order order;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        var user1 = new User(
                "Urlando",
                "urlando@gmail.com",
                "CUSTOMER",
                "password",
                "93555555"
        );

        var user2 = new User(
                "Jorge",
                "jorge@gmail.com",
                "RIDER",
                "password",
                "93555557"
        );

        this.customer = new Customer(
                user1,
                "Back street",
                "Very good restaurant, you can trust",
                "Restaurant",
                "PT50000201231234567890154"
        );

        this.order = new Order(
                "MBWAY",
                19.99f,
                this.customer
        );
        this.customer.getOrders().add(order);

        this.entityManager.persist(this.customer);
        this.entityManager.persist(order);
        this.entityManager.flush();
    }

    @Test
    void whenPaymentMethodEqualsCash_thenAExceptionShouldBeRaised() {
        Assertions.assertThatThrownBy(
                () -> this.entityManager.persistAndFlush(
                        new Order(
                                "CASH",
                                20.15f,
                                this.customer
                        )
                )
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void whenChangeOrderStatusWrong_thenExceptionShouldBeRaised() {
        var res = this.orderRepository.getById(this.order.getId());

        // update status of order to invalid value
        res.setStatus("INVALID");

        Assertions.assertThatThrownBy(
            () -> this.entityManager.persistAndFlush(res)
        ).isInstanceOf(PersistenceException.class);
    }
}