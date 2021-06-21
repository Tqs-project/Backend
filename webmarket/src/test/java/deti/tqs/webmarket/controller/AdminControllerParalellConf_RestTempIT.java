package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RiderRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class AdminControllerParalellConf_RestTempIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private deti.tqs.webmarket.cache.OrdersCache ordersCache;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
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
        riderRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();
        ordersCache.deleteAllOrders();
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
}
