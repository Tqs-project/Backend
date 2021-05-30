package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.util.Utils;
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
    private Order order2;

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

        this.customer = new Customer(
                user1,
                "Back street",
                "Very good restaurant, you can trust",
                "Restaurant",
                "PT50000201231234567890154"
        );

        this.order = new Order(
                "MBWAY",
                19.99,
                this.customer
        );
        this.order.setOrderTimestamp(Utils.parseTimestamp(
                "2021-05-26 00:00:00"
        ));

        this.order2 = new Order(
                "PAYPAL",
                21.14,
                this.customer
        );
        this.order2.setOrderTimestamp(Utils.parseTimestamp(
                "2021-05-29 00:00:00"
        ));

        this.customer.getOrders().add(this.order);
        this.customer.getOrders().add(this.order2);

        this.entityManager.persist(this.customer);
        this.entityManager.persist(this.order);
        this.entityManager.persist(this.order2);
        this.entityManager.flush();
    }

    @Test
    void whenPaymentMethodEqualsCash_thenAExceptionShouldBeRaised() {
        Assertions.assertThatThrownBy(
                () -> this.entityManager.persistAndFlush(
                        new Order(
                                "CASH",
                                20.15,
                                this.customer
                        )
                )
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void whenChangeOrderStatusWrong_thenExceptionShouldBeRaised() {
        var res = this.orderRepository.findById(this.order.getId()).get();

        // update status of order to invalid value
        res.setStatus("INVALID");

        Assertions.assertThatThrownBy(
            () -> this.entityManager.persistAndFlush(res)
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void getOrderAfterTimestampTest() {
        Assertions.assertThat(
                this.orderRepository.findOrdersByOrderTimestampAfter(
                        Utils.parseTimestamp(
                                "2021-05-27 00:00:00"
                        )
                )
        ).contains(this.order2).doesNotContain(this.order);
    }

    @Test
    void getOrderBeforeTimestampTest() {
        Assertions.assertThat(
                this.orderRepository.findOrdersByOrderTimestampBefore(
                        Utils.parseTimestamp(
                                "2021-05-27 00:00:00"
                        )
                )
        ).contains(this.order).doesNotContain(this.order2);
    }

    @Test
    void whenInvalidCostIsPassed_thenAExceptionShouldBeRaised() {
        var newOrder = new Order(
                "PAYPAL",
                10.001,
                this.customer
        );
        this.entityManager.persistAndFlush(newOrder);

        Assertions.assertThat(
                this.orderRepository.findById(newOrder.getId()).get()
        ).isEqualTo(this.order);
    }

}