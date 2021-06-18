package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.*;
import deti.tqs.webmarket.model.*;
import deti.tqs.webmarket.repository.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
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

    @Autowired
    private OrdersCache ordersCache;

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

        riderConcrete = new Rider(
                user,
                "aa-22-bb"
        );
        riderConcrete.setBusy(true);
        // TODO change to post and create rider
        user.setAuthToken("token");
        user.setRider(riderConcrete);

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
                customer,
                "Rua da Macieira, 15, Anadia 1111-111"
        );

        var ride = new Ride(
                order,
                "Far away from here"
        );
        order.setRide(ride);
        ride.setRider(riderConcrete);

        userRepository.saveAndFlush(client);
        userRepository.saveAndFlush(user);
        customerRepository.saveAndFlush(customer);
        riderRepository.saveAndFlush(riderConcrete);
        orderFromDB = orderRepository.saveAndFlush(order);
        rideRepository.saveAndFlush(ride);

        /**
         * TEST STARTS HERE
         */

        var headers = new HttpHeaders();
        headers.set("username", riderConcrete.getUser().getUsername());
        headers.set("idToken", riderConcrete.getUser().getAuthToken());
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
        ResponseEntity<RiderDto> response = restTemplate.postForEntity(
                "/api/riders", rider, RiderDto.class
        );

        var login = new CustomerLoginDto(
                rider.getUser().getUsername(),
                rider.getUser().getEmail(),
                rider.getUser().getPassword()
        );

        ResponseEntity<TokenDto> responseToken = restTemplate.postForEntity(
                "/api/riders/login", login, TokenDto.class
        );

        assertThat(responseToken.getStatusCode()).isEqualTo(
                HttpStatus.ACCEPTED
        );

        // verify if the token attribute was added to the customer row
        List<User> found = userRepository.findAll();
        assertThat(found).extracting(User::getAuthToken).isNotNull();
    }

    @Test
    void assignOrderToRiderTest() {
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

        var riderConcrete = new Rider(
                user,
                "aa-22-bb"
        );
        user.setRider(riderConcrete);

        var customer = new Customer(
                client,
                "right there",
                "dont even know",
                "Barber shop i think",
                "not important"
        );
        client.setCustomer(customer);

        // create customer
        restTemplate.postForEntity(
                "/api/customer",
                new CustomerCreateDto(
                        client.getUsername(),
                        client.getEmail(),
                        client.getPassword(),
                        client.getPhoneNumber(),
                        customer.getAddress(),
                        customer.getDescription(),
                        customer.getImageUrl(),
                        customer.getTypeOfService(),
                        customer.getIban()
                ),
                CustomerDto.class
        );
        // create rider
        restTemplate.postForEntity(
                "/api/riders",
                new RiderDto(
                    new UserDto(
                            user.getUsername(),
                            user.getEmail(),
                            "",
                            user.getPassword(),
                            user.getPhoneNumber()
                    ),
                        riderConcrete.getVehiclePlate()
                ),
                RiderDto.class
        );

        // make the rider login
        var login = new CustomerLoginDto(
                riderConcrete.getUser().getUsername(),
                riderConcrete.getUser().getEmail(),
                riderConcrete.getUser().getPassword()
        );

        var responseToken = restTemplate.postForEntity(
                "/api/riders/login", login, TokenDto.class
        );

        // make customer login
        var customerLogin = new CustomerLoginDto(
                client.getUsername(),
                null,
                client.getPassword()
        );

        var customerLoginToken = restTemplate.postForEntity(
                "/api/customer/signin", customerLogin, TokenDto.class
        );

        // create a order

        var headers = new HttpHeaders();
        headers.set("username", client.getUsername());
        headers.set("idToken", customerLoginToken.getBody().getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.postForEntity(
                "/api/order",
                new HttpEntity<>(new OrderCreateDto(
                        client.getUsername(),
                        "PAYPAL",
                        2.0,
                        "heaven"
                ), headers),
                OrderDto.class
        );

        assertThat(
                response.getBody()
        ).extracting(OrderDto::getPaymentType).isEqualTo(
                "PAYPAL"
        );

        assertThat(
                response.getBody()
        ).extracting(OrderDto::getStatus)
                .isEqualTo("WAITING");

        assertThat(
                response.getBody()
        ).extracting(OrderDto::getId).isNotNull();

        // check if the order was assigned to the rider

        var riderHeaders = new HttpHeaders();
        riderHeaders.set("username", riderConcrete.getUser().getUsername());
        riderHeaders.set("idToken", responseToken.getBody().getToken());
        riderHeaders.setContentType(MediaType.APPLICATION_JSON);

        var riderResponse = restTemplate.exchange(
                "/api/riders/order",
                HttpMethod.GET,
                new HttpEntity<>(riderHeaders),
                OrderDto.class
        );

        assertThat(
                riderResponse.getBody()
        ).extracting(OrderDto::getPaymentType)
                .isEqualTo("PAYPAL");

        assertThat(
                riderResponse.getBody()
        ).extracting(OrderDto::getStatus)
                .isEqualTo("WAITING");

        assertThat(
                riderResponse.getBody()
        ).extracting(OrderDto::getUsername)
                .isEqualTo(client.getUsername());

    }

    @Test
    void riderAcceptsAssignedOrderTest() {
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

        var riderConcrete = new Rider(
                user,
                "aa-22-bb"
        );
        user.setRider(riderConcrete);

        var customer = new Customer(
                client,
                "right there",
                "dont even know",
                "Barber shop i think",
                "not important"
        );
        client.setCustomer(customer);

        // create customer
        restTemplate.postForEntity(
                "/api/customer",
                new CustomerCreateDto(
                        client.getUsername(),
                        client.getEmail(),
                        client.getPassword(),
                        client.getPhoneNumber(),
                        customer.getAddress(),
                        customer.getDescription(),
                        customer.getImageUrl(),
                        customer.getTypeOfService(),
                        customer.getIban()
                ),
                CustomerDto.class
        );
        // create rider
        restTemplate.postForEntity(
                "/api/riders",
                new RiderDto(
                        new UserDto(
                                user.getUsername(),
                                user.getEmail(),
                                "",
                                user.getPassword(),
                                user.getPhoneNumber()
                        ),
                        riderConcrete.getVehiclePlate()
                ),
                RiderDto.class
        );

        // make the rider login
        var login = new CustomerLoginDto(
                riderConcrete.getUser().getUsername(),
                riderConcrete.getUser().getEmail(),
                riderConcrete.getUser().getPassword()
        );

        var responseToken = restTemplate.postForEntity(
                "/api/riders/login", login, TokenDto.class
        );

        // make customer login
        var customerLogin = new CustomerLoginDto(
                client.getUsername(),
                null,
                client.getPassword()
        );

        var customerLoginToken = restTemplate.postForEntity(
                "/api/customer/signin", customerLogin, TokenDto.class
        );

        // create a order

        var headers = new HttpHeaders();
        headers.set("username", client.getUsername());
        headers.set("idToken", customerLoginToken.getBody().getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.postForEntity(
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
        var riderHeaders = new HttpHeaders();
        riderHeaders.set("username", riderConcrete.getUser().getUsername());
        riderHeaders.set("idToken", responseToken.getBody().getToken());
        riderHeaders.setContentType(MediaType.APPLICATION_JSON);

        var riderResponse = restTemplate.exchange(
                "/api/riders/order",
                HttpMethod.GET,
                new HttpEntity<>(riderHeaders),
                OrderDto.class
        );

        assertThat(
                riderResponse.getBody()
        ).extracting(OrderDto::getId).isNotNull();

        // now, the rider accepts the assignment /api/riders/order/accept
        var acceptAssignmentResponse = restTemplate.postForEntity(
                "/api/riders/order/accept",
                new HttpEntity<>("", riderHeaders),
                String.class
        );

        assertThat(
                acceptAssignmentResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                acceptAssignmentResponse.getBody()
        ).isEqualTo("Have a nice ride");

        // check if the order status was changed
        var ordersFound = orderRepository.findAll();

        assertThat(
                ordersFound
        ).hasSize(1).extracting(Order::getStatus).containsOnly("DELIVERING");

        // check if the ride was created
        var ridesFound = rideRepository.findAll();

        assertThat(
                ridesFound
        ).hasSize(1).extracting(Ride::getDestination).containsOnly("heaven");

        // check if rider is busy

        var ridersFound = riderRepository.findAll();

        assertThat(
                ridersFound
        ).hasSize(1).extracting(Rider::getBusy).containsOnly(true);
    }

    @Test
    void riderDeclinesAssignedOrderTest() {
        var user = new User(
                "Albert",
                "albert@gmail.com",
                "RIDER",
                "password",
                "935666122"
        );

        var user2 = new User(
                "McQueen",
                "mcqueen@gmail.com",
                "RIDER",
                "password",
                "922123123"
        );

        var client = new User(
                "Not Albert",
                "notalbert@gmail.com",
                "CUSTOMER",
                "password",
                "935666125"
        );

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

        // create customer
        restTemplate.postForEntity(
                "/api/customer",
                new CustomerCreateDto(
                        client.getUsername(),
                        client.getEmail(),
                        client.getPassword(),
                        client.getPhoneNumber(),
                        customer.getAddress(),
                        customer.getDescription(),
                        customer.getImageUrl(),
                        customer.getTypeOfService(),
                        customer.getIban()
                ),
                CustomerDto.class
        );
        // create rider
        restTemplate.postForEntity(
                "/api/riders",
                new RiderDto(
                        new UserDto(
                                user.getUsername(),
                                user.getEmail(),
                                "",
                                user.getPassword(),
                                user.getPhoneNumber()
                        ),
                        riderConcrete.getVehiclePlate()
                ),
                RiderDto.class
        );

        // create second rider
        restTemplate.postForEntity(
                "/api/riders",
                new RiderDto(
                        new UserDto(
                                user2.getUsername(),
                                user2.getEmail(),
                                "",
                                user2.getPassword(),
                                user2.getPhoneNumber()
                        ),
                        riderConcrete2.getVehiclePlate()
                ),
                RiderDto.class
        );

        // make rider login
        var login = new CustomerLoginDto(
                riderConcrete.getUser().getUsername(),
                riderConcrete.getUser().getEmail(),
                riderConcrete.getUser().getPassword()
        );

        var responseToken = restTemplate.postForEntity(
                "/api/riders/login", login, TokenDto.class
        );

        // make second rider login
        var secondRiderLogin = new CustomerLoginDto(
                riderConcrete2.getUser().getUsername(),
                riderConcrete2.getUser().getEmail(),
                riderConcrete2.getUser().getPassword()
        );

        var secondRiderResponseLogin = restTemplate.postForEntity(
                "/api/riders/login", secondRiderLogin, TokenDto.class
        );

        // make customer login
        var customerLogin = new CustomerLoginDto(
                client.getUsername(),
                null,
                client.getPassword()
        );

        var customerLoginToken = restTemplate.postForEntity(
                "/api/customer/signin", customerLogin, TokenDto.class
        );

        // create a order

        var headers = new HttpHeaders();
        headers.set("username", client.getUsername());
        headers.set("idToken", customerLoginToken.getBody().getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.postForEntity(
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
        var riderHeaders = new HttpHeaders();
        riderHeaders.set("username", riderConcrete.getUser().getUsername());
        riderHeaders.set("idToken", responseToken.getBody().getToken());
        riderHeaders.setContentType(MediaType.APPLICATION_JSON);

        var riderResponse = restTemplate.exchange(
                "/api/riders/order",
                HttpMethod.GET,
                new HttpEntity<>(riderHeaders),
                OrderDto.class
        );

        assertThat(
                riderResponse.getBody()
        ).extracting(OrderDto::getId).isNotNull();

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
        var secondRiderHeaders = new HttpHeaders();
        secondRiderHeaders.set("username", riderConcrete2.getUser().getUsername());
        secondRiderHeaders.set("idToken", secondRiderResponseLogin.getBody().getToken());
        secondRiderHeaders.setContentType(MediaType.APPLICATION_JSON);

        var secondRiderResponse = restTemplate.exchange(
                "/api/riders/order",
                HttpMethod.GET,
                new HttpEntity<>(secondRiderHeaders),
                OrderDto.class
        );

        assertThat(
                secondRiderResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                secondRiderResponse.getBody()
        ).extracting(OrderDto::getId).isNotNull();
    }

    @Test
    void updateRiderLocationTest() {
        var user = new User(
            "Pablo",
                "pablo@gmail.com",
                "RIDER",
                "password",
                "999999999"
        );
        var rider = new Rider(
                user,
                "plate"
        );
        user.setAuthToken("token");
        user.setRider(rider);

        userRepository.saveAndFlush(user);
        riderRepository.saveAndFlush(rider);

        var headers = new HttpHeaders();
        headers.set("username", user.getUsername());
        headers.set("idToken", user.getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.postForEntity(
                "/api/riders/location",
                    new HttpEntity<>(
                            new LocationDto(
                                    "2",
                                    "4"
                            ),
                            headers
                    ),
                String.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).isEqualTo("Location updated successfully.");

        var found = riderRepository.findAll();

        assertThat(
                found
        ).hasSize(1).extracting(Rider::getLat).containsOnly("2");

        assertThat(
                found
        ).extracting(Rider::getLng).containsOnly("4");
    }

    @Test
    void getInfoRiderTest() {
        var user = new User(
                "Pablo",
                "pablo@gmail.com",
                "RIDER",
                "password",
                "999999999"
        );
        var rider = new Rider(
                user,
                "plate"
        );
        user.setAuthToken("token");
        user.setRider(rider);

        userRepository.saveAndFlush(user);
        var riderGetTheId = riderRepository.saveAndFlush(rider);

        var headers = new HttpHeaders();
        headers.set("username", user.getUsername());
        headers.set("idtoken", user.getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/riders",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RiderFullInfoDto.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).extracting("id")
                .isEqualTo(riderGetTheId.getId());
    }
}
