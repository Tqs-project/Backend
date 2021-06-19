package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.UserRepository;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class AdminController_RestTemplateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private deti.tqs.webmarket.cache.OrdersCache ordersCache;

    private User user;

    @BeforeEach
    void setUp() {
        var user = new User(
                "Ronaldo",
                "ronaldo@mail.com",
                "ADMIN",
                "password",
                "999999999"
        );
        user.setAuthToken("token");
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();
        ordersCache.deleteAllOrders();
    }

    @Test
    void getCurrentAssignments() {
        var username = "Ronaldo";
        var orderId = 10L;
        ordersCache.assignOrder(username, orderId);

        var headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/admin/orderscache/assignments",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).hasSize(1).extracting(username).isEqualTo(10);
    }

    @Test
    void getWaitingQueueOrders() {
        var username = "Ronaldo";
        var orderId = 10L;
        ordersCache.addOrderToQueue(orderId);

        var headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/admin/orderscache/waitingqueue",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).hasSize(1).containsOnly(10);
    }

    @Test
    void getCustomersTest() {
        var user1 = new User(
                "Pepe",
                "cabecadas@mail.com",
                "CUSTOMER",
                "password",
                "999999999"
        );
        var customer1 = new Customer(
                user1,
                "address",
                "desc",
                "tos",
                "iban"
        );
        user1.setCustomer(customer1);

        var user2 = new User(
                "Rui Patrício",
                "ruizao@mail.com",
                "CUSTOMER",
                "password",
                "999999999"
        );
        var customer2 = new Customer(
                user2,
                "address",
                "desc",
                "tos",
                "iban"
        );
        user2.setCustomer(customer2);

        userRepository.save(user1);
        userRepository.saveAndFlush(user2);
        customerRepository.save(customer1);
        customerRepository.saveAndFlush(customer2);

        var headers = new HttpHeaders();
        headers.set("username", "Ronaldo");
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/admin/customers",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CustomerDto[].class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).hasSize(2).extracting(CustomerDto::getUsername)
                .containsExactly("Pepe", "Rui Patrício");
    }

    @Test
    void getOrdersTest() {
        var user1 = new User(
                "Pepe",
                "cabecadas@mail.com",
                "CUSTOMER",
                "password",
                "999999999"
        );
        var customer1 = new Customer(
                user1,
                "address",
                "desc",
                "tos",
                "iban"
        );
        user1.setCustomer(customer1);

        userRepository.saveAndFlush(user1);
        customerRepository.saveAndFlush(customer1);

        // create order
        var order = new Order(
                "PAYPAL",
                2.11,
                customer1,
                "here next to me"
        );
        var order2 = new Order(
                "PAYPAL",
                3.11,
                customer1,
                "here next to me"
        );

        orderRepository.saveAndFlush(order);
        orderRepository.saveAndFlush(order2);

        var headers = new HttpHeaders();
        headers.set("username", "Ronaldo");
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/admin/orders",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                OrderDto[].class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).hasSize(2).extracting(OrderDto::getCost)
                .containsExactly(2.11, 3.11);

    }

}