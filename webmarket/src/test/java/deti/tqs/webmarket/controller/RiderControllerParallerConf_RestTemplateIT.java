package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.OrderCreateDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.UserDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.*;
import lombok.extern.log4j.Log4j2;
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

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class RiderControllerParallerConf_RestTemplateIT {

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

    @Autowired
    private deti.tqs.webmarket.cache.OrdersCache ordersCache;

    private Order orderFromDB;
    private Rider riderConcrete;
    private RiderDto rider;
    private UserDto user;

    @BeforeEach
    void setUp() {
        user = new UserDto();
        rider = new RiderDto();
        user.setUsername("Rafa21");
        user.setEmail("rafael@gmail.com");
        user.setPassword("password");
        user.setPhoneNumber("945623112");
        user.setRole("RIDER");
        rider.setUser(user);
        rider.setVehiclePlate("FF-32-H8");
    }

    @AfterEach
    void tearDown() {
        rideRepository.deleteAll();
        orderRepository.deleteAll();
        riderRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();
        ordersCache.deleteAllOrders();
    }

    @Test
    void riderDeclinesAssignedOrderTest() {
        var token = "token_secret";
        var user = new User(
                "Albert",
                "albert@gmail.com",
                "RIDER",
                "password",
                "935666122"
        );
        user.setAuthToken(token);

        var user2 = new User(
                "McQueen",
                "mcqueen@gmail.com",
                "RIDER",
                "password",
                "922123123"
        );
        user2.setAuthToken(token);

        var client = new User(
                "Not Albert",
                "notalbert@gmail.com",
                "CUSTOMER",
                "password",
                "935666125"
        );
        client.setAuthToken(token);

        var riderConcrete = new Rider(
                user,
                "aa-22-bb"
        );
        user.setRider(riderConcrete);

        var riderConcrete2 = new Rider(
                user2,
                "ff-99-ff"
        );
        user2.setRider(riderConcrete2);

        var customer = new Customer(
                client,
                "right there",
                "dont even know",
                "Barber shop i think",
                "not important"
        );
        client.setCustomer(customer);

        var userFromDB1 = userRepository.saveAndFlush(user);
        var userFromDB2 = userRepository.saveAndFlush(user2);
        var clientFromDB = userRepository.saveAndFlush(client);
        riderRepository.saveAndFlush(riderConcrete);
        riderRepository.saveAndFlush(riderConcrete2);
        customerRepository.saveAndFlush(customer);

        // create a order
        var headers = new HttpHeaders();
        headers.set("username", client.getUsername());
        headers.set("idToken", client.getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var orderResponse = restTemplate.postForEntity(
                "/api/order",
                new HttpEntity<>(new OrderCreateDto(
                        client.getUsername(),
                        "PAYPAL",
                        2.0,
                        "heaven"
                ), headers),
                OrderDto.class
        );


        // so the order was assigned to the rider
        assertThat(
                this.ordersCache.riderHasNewAssignments(user.getUsername())
        ).isTrue();

        var riderHeaders = new HttpHeaders();
        riderHeaders.set("username", user.getUsername());
        riderHeaders.set("idToken", user.getAuthToken());
        riderHeaders.setContentType(MediaType.APPLICATION_JSON);

        // now, the rider declines the assignment
        var acceptAssignmentResponse = restTemplate.postForEntity(
                "/api/riders/order/decline",
                new HttpEntity<>("", riderHeaders),
                String.class
        );

        assertThat(
                acceptAssignmentResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);


        // check if the order was assigned to the second rider
        assertThat(
                this.ordersCache.riderHasNewAssignments(user.getUsername())
        ).isFalse();

        assertThat(
                this.ordersCache.riderHasNewAssignments(user2.getUsername())
        ).isTrue();

        // check the id of the order
        assertThat(
                this.ordersCache.retrieveAssignedOrder(user2.getUsername())
        ).isEqualTo(orderResponse.getBody().getId());
    }
}
