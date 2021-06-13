package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImpTest {

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderServiceImp orderServiceImp;

    private Order order;
    private Customer customer;
    private OrderDto orderCreateDto;
    
    @BeforeEach
    void setUp() {
        orderCreateDto = new OrderDto();
        orderCreateDto.setId(4L);
        orderCreateDto.setPaymentType("MB");
        orderCreateDto.setUsername("Maria");
        orderCreateDto.setEmail("maria@gmail.com");
        orderCreateDto.setLocation("Rua da Macieira, 15");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder() {

    }
}