package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.model.*;
import deti.tqs.webmarket.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class RiderController_RestTemplateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Order orderFromDB;
    private Rider rider;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        rideRepository.deleteAll();
        orderRepository.deleteAll();
        riderRepository.deleteAll();
        customerRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void updateOrderDelivered() {
        var user = new User(
                "Albert",
                "albert@gmail.com",
                "RIDER",
                "password",
                "935666122"
        );

        var client = new User(
                "Not Albert",
                "notalbert@gmail.com",
                "CUSTOMER",
                "password",
                "935666125"
        );

        rider = new Rider(
                user,
                "aa-22-bb"
        );
        rider.setBusy(true);
        // TODO change to post and create rider
        user.setAuthToken("token");
        user.setRider(rider);

        var customer = new Customer(
                client,
                "right there",
                "dont even know",
                "Barber shop i think",
                "not important"
        );
        client.setCustomer(customer);

        var order = new Order(
                "PAYPAL",
                20.0,
                customer
        );

        var ride = new Ride(
                order,
                "Far away from here"
        );
        order.setRide(ride);
        ride.setRider(rider);

        userRepository.saveAndFlush(client);
        userRepository.saveAndFlush(user);
        customerRepository.saveAndFlush(customer);
        riderRepository.saveAndFlush(rider);
        orderFromDB = orderRepository.saveAndFlush(order);
        rideRepository.saveAndFlush(ride);

        /**
         * TEST STARTS HERE
         */

        var headers = new HttpHeaders();
        headers.set("username", rider.getUser().getUsername());
        headers.set("idToken", rider.getUser().getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.postForEntity(
                "/api/rider/ride/" + orderFromDB.getId() + "/delivered",
                new HttpEntity<>("", headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(
                HttpStatus.OK
        );

        assertThat(response.getBody()).hasToString(
                "Ride updated with success"
        );

        // check if the busy status on the rider was updated
        var found = riderRepository.findAll();
        assertThat(found).hasSize(1).extracting(Rider::getBusy)
            .containsOnly(false);

        // check if the order status was also updated
        var orders = orderRepository.findAll();
        assertThat(orders).hasSize(1).extracting(Order::getStatus)
                .containsOnly("DELIVERED");
    }
}