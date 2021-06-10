package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.TokenDto;
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

    private CustomerDto customer;
    private CustomerDto customerResponse;

    @BeforeEach
    void setUp() {
        customerResponse = new CustomerDto(
                3L,
                "Pedro",
                "pedro@gmail.com",
                "CUSTOMER",
                "",
                "935111111",
                "Front Street",
                "Wonderful coffee shop",
                null,
                "Coffee",
                "PT50000201231234567890155",
                new ArrayList<>(),
                new ArrayList<>()
        );

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
                .content(JsonUtil.toJson(customer))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", CoreMatchers.is(customer.getUsername())))
                .andExpect(jsonPath("$.password", CoreMatchers.is("")))
                .andExpect(jsonPath("$.role", CoreMatchers.is("CUSTOMER")));

        Mockito.verify(customerService, Mockito.times(1)).createCustomer(customer);
    }

    @Test
    void whenPutCustomer_thenReturnCustomerUpdated() throws Exception {
        customer.setDescription("A brand new description");
        customerResponse.setDescription("A brand new description");
        Mockito.when(customerService.updateCustomer(customer))
            .thenReturn(customerResponse);

        mvc.perform(
                put("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(customer))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", CoreMatchers.is(customer.getUsername())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(customer.getDescription())));

        Mockito.verify(customerService, Mockito.times(1)).updateCustomer(customer);
    }

    @Test
    void whenPostCustomerLogin_ThenReturnToken() throws Exception {
        var token = new TokenDto("encrypted-token", "");

        Mockito.when(customerService.login(customer))
                .thenReturn(token);

        mvc.perform(
                post("/api/customer/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(customer))
        ).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.token", CoreMatchers.is(token.getToken())));

        Mockito.verify(customerService, Mockito.times(1)).login(customer);
    }

    @Test
    void whenPostCustomerLoginWithError_thenReturnEmptyToken() throws Exception {
        var token = new TokenDto("", "Some error occurred");

        Mockito.when(customerService.login(customer))
                .thenReturn(token);

        mvc.perform(
                post("/api/customer/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(customer))
        ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.token", CoreMatchers.is("")))
            .andExpect(jsonPath("$.errorMessage", CoreMatchers.is(token.getErrorMessage())));

        Mockito.verify(customerService, Mockito.times(1)).login(customer);
    }
}