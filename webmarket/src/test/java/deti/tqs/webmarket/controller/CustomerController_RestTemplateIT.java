package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class CustomerController_RestTemplateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    private CustomerDto customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerDto();
        customer.setUsername("Pedro");
        customer.setEmail("pedro@gmail.com");
        customer.setPassword("password");
        customer.setPhoneNumber("935777777");
        customer.setAddress("Front Street");
        customer.setDescription("Beautiful landscape");
        customer.setTypeOfService("Coffee");
        customer.setIban("PT50000201231234567890155");
    }

    // assertThat(response.getBody()).extracting(class::getName).containsExactly("", "");

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenCreateCustomerIsValid_thenCreateCustomer() {
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "/api/customer", customer, CustomerDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(
                HttpStatus.CREATED
        );

        List<Customer> found = customerRepository.findAll();
        assertThat(found).extracting(Customer::getIban).containsOnly(
                customer.getIban()
        );
    }


    @Test
    void whenUpdateCustomer_thenTheCustomerShouldBeUpdatedOnDB() {
        // TODO change for customerRepo.saveandflush()
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "/api/customer", customer, CustomerDto.class
        );

        customer.setDescription("A brand new description");
        restTemplate.put(
                "/api/customer", customer, CustomerDto.class
        );

        Optional<User> found = userRepository.findByUsername(
                customer.getUsername()
        );

        var foundCustomer = found.get().getCustomer();
        assertThat(foundCustomer.getDescription()).isEqualTo(
                customer.getDescription()
        );
    }

    @Test
    void whenCustomerMakesLogin_thenTheTokenShouldBePersistedOnDB() {
        // TODO change to saveandflush
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "/api/customer", customer, CustomerDto.class
        );

        ResponseEntity<TokenDto> responseToken = restTemplate.postForEntity(
                "/api/customer/signin", customer, TokenDto.class
        );

        assertThat(responseToken.getStatusCode()).isEqualTo(
                HttpStatus.ACCEPTED
        );

        // verify if the token attribute was added to the customer row
        var found = userRepository.findAll();
        assertThat(found).extracting(User::getAuthToken).isNotNull();
    }
}