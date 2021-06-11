package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.dto.UserDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.repository.RiderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

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
    private RiderRepository riderRepository;

    @Autowired
    private UserRepository userRepository;

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
