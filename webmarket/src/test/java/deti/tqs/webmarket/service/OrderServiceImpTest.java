package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private CustomerRepository customerRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImp orderServiceImp;


    private User user;
    private Customer customer;

    private CustomerDto customerCreateDto;

    private OrderDto orderCreateDto;
    private OrderDto orderCreateDtoRet;
    private Order order;
    private Order orderFromDB;

    @BeforeEach
    void setUp() {
        user = new User("Maria", "maria@gmail.com", "CUSTOMER", "", "935111111");
        user.setId(3L);
        customer = new Customer(user, "Front Street", "Wonderful coffee shop", "Coffee", "PT50000201231234567890155");
        customer.setId(user.getId());

        customerCreateDto = new CustomerDto(
                3L,
                "Maria",
                "maria@gmail.com",
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

        orderCreateDto = new OrderDto(
                4L,
                "MB",
                15.6,
                "Maria",
                "maria@gmail.com",
                "Rua da Macieira, 15"
        );

        orderCreateDtoRet = new OrderDto(
                4L,
                "MB",
                15.6,
                "Maria",
                "maria@gmail.com",
                "Rua da Macieira, 15"
        );

        order = new Order(orderCreateDto.getPaymentType(), orderCreateDto.getCost(), customer, orderCreateDto.getLocation());

        orderFromDB = new Order(orderCreateDto.getPaymentType(), orderCreateDto.getCost(), customer, orderCreateDto.getLocation());
        orderFromDB.setId(orderCreateDto.getId());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder_AddToDB_Test() {
        Mockito.when(userRepository.findByUsername("Maria")).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(customerRepository.findByUser_Email("maria@gmail.com")).thenReturn(customer);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(orderFromDB);
        assertThat(orderServiceImp.createOrder(orderCreateDto)).isEqualTo(orderCreateDtoRet);
    }


}