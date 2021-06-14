package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.*;
import deti.tqs.webmarket.util.Utils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class RideRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    private Ride ride;
    private Ride ride2;
    private Rider rider;

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
                "José",
                "jose@gmail.com",
                "RIDER",
                "password",
                "93555554"
        );

        var customer = new Customer(
                user1,
                "Back street",
                "Very good restaurant, you can trust",
                "Restaurant",
                "PT50000201231234567890154"
        );

        this.rider = new Rider(
                user2, "00-AA-22"
        );

        var order = new Order(
                "MBWAY",
                19.99,
                customer,
                "Rua da Macieira, 15, Anadia 1111-111"
        );
        order.setOrderTimestamp(Utils.parseTimestamp(
                "2021-05-26 00:00:00"
        ));

        var order2 = new Order(
                "PAYPAL",
                21.14,
                customer,
                "Rua da Pereira, 16, Anadia 1111-111"
        );
        order2.setOrderTimestamp(Utils.parseTimestamp(
                "2021-05-29 00:00:00"
        ));

        customer.getOrders().add(order);
        customer.getOrders().add(order2);

        this.ride = new Ride(order, "Atlantic street");
        this.ride2 = new Ride(order2, "Hell street");

        // assign ride to rider
        this.rider.getRides().add(this.ride);
        this.ride.setRider(this.rider);

        this.entityManager.persist(customer);
        this.entityManager.persist(order);
        this.entityManager.persist(order2);
        this.entityManager.persist(this.ride);
        this.entityManager.persist(this.ride2);
        this.entityManager.persist(this.rider);
        this.entityManager.flush();
    }

    @Test
    void findRidesByDestinationTest() {
        Assertions.assertThat(
                this.rideRepository.findRidesByDestination("Hell street")
        ).contains(this.ride2).doesNotContain(this.ride);
    }

    @Test
    void findAllRidesOfARiderTest() {
        Assertions.assertThat(
                this.rideRepository.findRidesByRider(this.rider)
        ).contains(this.ride).doesNotContain(this.ride2);
    }
}