package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder() {
        UserDto userDto = new UserDto();
        CustomerDto customerDto = new CustomerDto();
        OrderDto orderDto = new OrderDto();

        userDto.setUsername("drinkUp");
        userDto.setEmail("drinkup@gmail.com");
        userDto.setRole("CUSTOMER");
        userDto.setPassword("pass");
        userDto.setPhoneNumber("+351 938736");

        customerDto.setUser(userDto);
        customerDto.setDescription("drink's store");
        customerDto.setTypeOfService("Drinks");
        customerDto.setIban("PT2383288");

        orderDto.setPaymentType("MB");
        orderDto.setCost(100);
        orderDto.setCustomer(customerDto);
        orderDto.setLocation("Rua da Anadia");


    }

    @Test
    void getOrders() {
    }
}