package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.*;
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
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private CustomerCreateDto customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerCreateDto();
        customer.setUsername("Pedro");
        customer.setEmail("pedro@gmail.com");
        customer.setPassword("password");
        customer.setPhoneNumber("935777777");
        customer.setAddress("Front Street");
        customer.setDescription("Beautiful landscape");
        customer.setTypeOfService("Coffee");
        customer.setIban("PT50000201231234567890155");
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
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

        var userOptional = userRepository.findByUsername(customer.getUsername());

        assertThat(userOptional).isPresent();

        var user = userOptional.get();
        user.setAuthToken("secret_token");

        userRepository.saveAndFlush(user);

        customer.setDescription("A brand new description");

        var headers = new HttpHeaders();
        headers.set("username", customer.getUsername());
        headers.set("idToken", "secret_token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.put(
                "/api/customer", new HttpEntity<>(customer, headers), CustomerDto.class
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
    void getCustomerInformationTest() {
        var user = new User(
                customer.getUsername(),
                customer.getEmail(),
                "CUSTOMER",
                customer.getPassword(),
                customer.getPhoneNumber()
        );
        var customerConcrete = new Customer(
                user,
                customer.getAddress(),
                customer.getDescription(),
                customer.getTypeOfService(),
                customer.getIban()
        );
        user.setCustomer(customerConcrete);
        user.setAuthToken("token");

        userRepository.saveAndFlush(user);
        customerRepository.saveAndFlush(customerConcrete);

        var headers = new HttpHeaders();
        headers.set("username", customer.getUsername());
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/customer",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CustomerDto.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).extracting(CustomerDto::getUsername)
                .isEqualTo(customer.getUsername());
    }

    @Test
    void whenCustomerMakesLogin_thenTheTokenShouldBePersistedOnDB() {
        // TODO change to saveandflush
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "/api/customer", customer, CustomerDto.class
        );

        var login = new CustomerLoginDto(
                customer.getUsername(),
                null,
                customer.getPassword()
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

    @Test
    void getOrderTest() {
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "/api/customer", customer, CustomerDto.class
        );

        var userId = response.getBody().getId();
        System.out.println(userId);

        var userOptional = userRepository.findById(userId);

        assertThat(userOptional).isPresent();

        var user = userOptional.get();
        user.setAuthToken("token");

        userRepository.saveAndFlush(user);

        // create a order
        var headers = new HttpHeaders();
        headers.set("username", customer.getUsername());
        headers.set("idToken", user.getAuthToken());

        var createOrderResponse = restTemplate.postForEntity(
                "/api/order",
                new HttpEntity<>(
                        new OrderCreateDto(
                            user.getUsername(),
                                "PAYPAL",
                                2.0,
                                "Candy Land"
                        ),
                        headers
                ),
                OrderDto.class
        );

        assertThat(
                createOrderResponse.getStatusCode()
        ).isEqualTo(HttpStatus.CREATED);

        // return the order previously created
        var responseOrderDto = restTemplate.exchange(
                "/api/customer/orders/" + createOrderResponse.getBody().getId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                OrderDto.class
        );

        assertThat(responseOrderDto.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(
                responseOrderDto.getBody()
        ).extracting(OrderDto::getStatus)
                .isEqualTo("WAITING");

        assertThat(
                responseOrderDto.getBody()
        ).extracting(OrderDto::getUsername)
                .isEqualTo(customer.getUsername());
    }

    @Test
    void getAllCustomerOrdersTest() {
        var user = new User(
                customer.getUsername(),
                customer.getEmail(),
                "CUSTOMER",
                customer.getPassword(),
                customer.getPhoneNumber()
        );
        var customerConcrete = new Customer(
                user,
                customer.getAddress(),
                customer.getDescription(),
                customer.getTypeOfService(),
                customer.getIban()
        );
        user.setCustomer(customerConcrete);
        user.setAuthToken("token");

        userRepository.saveAndFlush(user);
        customerRepository.saveAndFlush(customerConcrete);

        var order1 = new Order(
                "PAYPAL",
                2.34,
                customerConcrete,
                "Candy Shop Center"
        );
        var order2 = new Order(
                "PAYPAL",
                3.12,
                customerConcrete,
                "I dont know"
        );
        orderRepository.save(order1);
        orderRepository.saveAndFlush(order2);

        var headers = new HttpHeaders();
        headers.set("username", user.getUsername());
        headers.set("idToken", "token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(
                "/api/customer/orders",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                OrderDto[].class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).hasSize(2).extracting(OrderDto::getLocation)
                .containsExactly("Candy Shop Center", "I dont know");
    }

    @Test
    void returnPriceOfOrderTest() {
        var user = new User(
                customer.getUsername(),
                customer.getEmail(),
                "CUSTOMER",
                customer.getPassword(),
                customer.getPhoneNumber()
        );
        var customerConcrete = new Customer(
                user,
                "Porto, Portugal",
                customer.getDescription(),
                customer.getTypeOfService(),
                customer.getIban()
        );
        user.setCustomer(customerConcrete);
        user.setAuthToken("token");

        userRepository.saveAndFlush(user);
        customerRepository.saveAndFlush(customerConcrete);

        var headers = new HttpHeaders();
        headers.set("username", user.getUsername());
        headers.set("idToken", user.getAuthToken());

        var destination = "Lisboa, Portugal";

        var response = restTemplate.exchange(
                "/api/customer/deliveryprice?destination=" + destination,
                HttpMethod.GET,
                    new HttpEntity<>(headers),
                PriceEstimationDto.class
        );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                response.getBody()
        ).extracting(PriceEstimationDto::getOrigin)
                .isEqualTo("Porto, Portugal");

        assertThat(
                response.getBody()
        ).extracting(PriceEstimationDto::getDistanceM)
                .isEqualTo(313413L);

    }
}