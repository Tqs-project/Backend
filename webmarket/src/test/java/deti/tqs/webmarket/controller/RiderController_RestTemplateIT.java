package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.model.*;
import deti.tqs.webmarket.repository.*;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.dto.UserDto;
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
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    private RiderDto rider;
    private UserDto user;

    @BeforeEach
    void setUp() {
        riderRepository.deleteAll();
        userRepository.deleteAll();

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
                "/api/riders/ride/" + orderFromDB.getId() + "/delivered",
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

    @Test
    void whenCreateRiderIsValid_thenCreateRider() {
        ResponseEntity<RiderDto> response = restTemplate.postForEntity(
                "/api/riders", rider, RiderDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(
                HttpStatus.CREATED
        );

        List<Rider> found = riderRepository.findAll();
        assertThat(found).extracting(Rider::getVehiclePlate).containsOnly(
                rider.getVehiclePlate()
        );
    }
    
    @Test
    void whenRiderMakesLogin_thenTheTokenShouldBePersistedOnDB() {
        // TODO change to saveandflush
        ResponseEntity<RiderDto> response = restTemplate.postForEntity(
                "/api/riders", rider, RiderDto.class
        );

        ResponseEntity<TokenDto> responseToken = restTemplate.postForEntity(
                "/api/riders/login", rider, TokenDto.class
        );

        assertThat(responseToken.getStatusCode()).isEqualTo(
                HttpStatus.ACCEPTED
        );

        // verify if the token attribute was added to the customer row
        List<Rider> found = riderRepository.findAll();
        assertThat(found).extracting(Rider::getAuthToken).isNotNull();
    }
}
