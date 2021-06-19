package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.model.User;
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

}