package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerCreateDto;
import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.CustomerLoginDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.CustomerService;
import deti.tqs.webmarket.util.JsonUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(CustomerController.class)
class CustomerController_WithMockServiceIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserRepository userRepository;

    private CustomerDto customer;
    private CustomerDto customerResponse;
    private CustomerCreateDto customerCreate;
    private User user;

    @BeforeEach
    void setUp() {
        customerResponse = new CustomerDto(
                3L,
                "Pedro",
                "pedro@gmail.com",
                "CUSTOMER",
                "",
                "935111111",
                null,
                "Front Street",
                "Wonderful coffee shop",
                null,
                "Coffee",
                "PT50000201231234567890155",
                new ArrayList<>(),
                new ArrayList<>()
        );

        user = new User(
                customerResponse.getUsername(),
                customerResponse.getEmail(),
                customerResponse.getRole(),
                "password",
                customerResponse.getPhoneNumber()
        );
        user.setAuthToken("token");

        customerCreate = new CustomerCreateDto();
        customerCreate.setUsername(customerResponse.getUsername());
        customerCreate.setEmail(customerResponse.getEmail());
        customerCreate.setPassword("password");
        customerCreate.setPhoneNumber(customerResponse.getPhoneNumber());
        customerCreate.setAddress(customerResponse.getAddress());
        customerCreate.setDescription(customerResponse.getDescription());
        customerCreate.setTypeOfService(customerResponse.getTypeOfService());
        customerCreate.setIban(customerResponse.getIban());

        customer = new CustomerDto();
        customer.setUsername(customerResponse.getUsername());
        customer.setEmail(customerResponse.getEmail());
        customer.setPassword("password");
        customer.setPhoneNumber(customerResponse.getPhoneNumber());
        customer.setAddress(customerResponse.getAddress());
        customer.setDescription(customerResponse.getDescription());
        customer.setTypeOfService(customerResponse.getTypeOfService());
        customer.setIban(customerResponse.getIban());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenPostCustomer_thenCreateCustomer() throws Exception{
        Mockito.when(customerService.createCustomer(customer))
                .thenReturn(customerResponse);

        mvc.perform(
                post("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(customerCreate))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", CoreMatchers.is(customer.getUsername())))
                .andExpect(jsonPath("$.password", CoreMatchers.is("")))
                .andExpect(jsonPath("$.role", CoreMatchers.is("CUSTOMER")));

        Mockito.verify(customerService, Mockito.times(1)).createCustomer(customer);
    }

    @Test
    void whenPutCustomer_thenReturnCustomerUpdated() throws Exception {
        customerCreate.setDescription("A brand new description");
        customer.setDescription("A brand new description");
        customerResponse.setDescription("A brand new description");
        Mockito.when(customerService.updateCustomer(customer))
            .thenReturn(customerResponse);

        Mockito.when(userRepository.findByUsername(customerCreate.getUsername()))
                .thenReturn(Optional.of(user));

        mvc.perform(
                put("/api/customer")
                        .header("username", customer.getUsername())
                        .header("idToken", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(customerCreate))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", CoreMatchers.is(customerCreate.getUsername())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(customerCreate.getDescription())));

        Mockito.verify(customerService, Mockito.times(1)).updateCustomer(customer);
    }

    @Test
    void whenPostCustomerLogin_ThenReturnToken() throws Exception {
        var token = new TokenDto("encrypted-token", "");

        var login = new CustomerLoginDto(
                customer.getUsername(),
                customer.getEmail(),
                customer.getPassword()
        );

        var customerLogin = new CustomerDto();
        customerLogin.setUsername(login.getUsername());
        customerLogin.setEmail(login.getEmail());
        customerLogin.setPassword(login.getPassword());

        Mockito.when(customerService.login(customerLogin))
                .thenReturn(token);

        mvc.perform(
                post("/api/customer/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(login))
        ).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.token", CoreMatchers.is(token.getToken())));

        Mockito.verify(customerService, Mockito.times(1)).login(customerLogin);
    }

    @Test
    void whenPostCustomerLoginWithError_thenReturnEmptyToken() throws Exception {
        var token = new TokenDto("", "Some error occurred");

        var login = new CustomerLoginDto(
                customer.getUsername(),
                customer.getEmail(),
                customer.getPassword()
        );

        var customerLogin = new CustomerDto();
        customerLogin.setUsername(login.getUsername());
        customerLogin.setEmail(login.getEmail());
        customerLogin.setPassword(login.getPassword());

        Mockito.when(customerService.login(customerLogin))
                .thenReturn(token);

        mvc.perform(
                post("/api/customer/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(login))
        ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.token", CoreMatchers.is("")))
            .andExpect(jsonPath("$.errorMessage", CoreMatchers.is(token.getErrorMessage())));

        Mockito.verify(customerService, Mockito.times(1)).login(customerLogin);
    }
}