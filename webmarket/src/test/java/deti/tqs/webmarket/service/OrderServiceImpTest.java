package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Rider;
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

import java.util.Arrays;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private CustomerRepository customerRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private OrdersCache ordersCache;

    @InjectMocks
    private OrderServiceImp orderServiceImp;


    private User user;
    private Customer customer;

    private CustomerDto customerCreateDto;

    private OrderDto orderCreateDto;

    private User userAssignment1;
    private User userAssignment2;
    private User userAssignment3;
    private User userAssignment4;
    private Order orderAssignment;

    private OrderDto orderCreateDtoRet;
    private Order order;
    private Order orderFromDB;

    @BeforeEach
    void setUp() {
        user = new User("Maria", "maria@gmail.com", "CUSTOMER", "", "935111111");
        user.setId(3L);
        customer = new Customer(user, "Front Street", "Wonderful coffee shop", "Coffee", "PT50000201231234567890155");
        customer.setId(user.getId());
        user.setCustomer(customer);

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

        orderCreateDto = new OrderDto();
        orderCreateDto.setId(4L);
        orderCreateDto.setPaymentType("MB");
        orderCreateDto.setCost(15.6);
        orderCreateDto.setUsername("Maria");
        orderCreateDto.setLocation("Rua da Macieira, 15");

        orderCreateDtoRet = new OrderDto();
        orderCreateDtoRet.setId(4L);
        orderCreateDtoRet.setPaymentType("MB");
        orderCreateDtoRet.setCost(15.6);
        orderCreateDtoRet.setUsername("Maria");
        orderCreateDtoRet.setLocation("Rua da Macieira, 15");
        orderCreateDtoRet.setStatus("WAITING");
        orderCreateDtoRet.setCustomerId(3L);

        order = new Order(orderCreateDto.getPaymentType(), orderCreateDto.getCost(), customer, orderCreateDto.getLocation());

        orderFromDB = new Order(orderCreateDto.getPaymentType(), orderCreateDto.getCost(), customer, orderCreateDto.getLocation());
        orderFromDB.setId(orderCreateDto.getId());

        orderAssignment = new Order();
        orderAssignment.setId(22L);
        var riderAssignment1 = new Rider(null, "plate");
        var riderAssignment2 = new Rider(null, "plate");
        var riderAssignment3 = new Rider(null, "plate");
        var riderAssignment4 = new Rider(null, "plate");

        userAssignment1 = new User();
        userAssignment1.setUsername("user1");
        userAssignment1.setRider(riderAssignment1);
        userAssignment2 = new User();
        userAssignment2.setUsername("user2");
        userAssignment2.setRider(riderAssignment2);
        userAssignment3 = new User();
        userAssignment3.setUsername("user3");
        userAssignment3.setRider(riderAssignment3);
        userAssignment4 = new User();
        userAssignment4.setUsername("user4");
        userAssignment4.setRider(riderAssignment4);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder_AddToDB_Test() {
        Mockito.when(userRepository.findByUsername("Maria")).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(orderFromDB);

        var res = orderServiceImp.createOrder(orderCreateDto);
        res.setOrderTimestamp(null);
        orderCreateDtoRet.setCustomerLocation("Front Street");
        assertThat(res).isEqualTo(orderCreateDtoRet);
    }

    @Test
    void whenAllTheRidersAreAvailable_ThenTheRider1ShouldBeAssignedWithTheOrder() {
        Mockito.when(userRepository.getRidersLogged()).thenReturn(
                Arrays.asList(userAssignment1,
                        userAssignment2,
                        userAssignment3,
                        userAssignment4)
        );

        Mockito.when(ordersCache.riderHasNewAssignments(
                userAssignment1.getUsername()
        )).thenReturn(false);

        orderServiceImp.assignOrderToRider(orderAssignment);

        Mockito.verify(userRepository, Mockito.times(1))
                .getRidersLogged();

        Mockito.verify(ordersCache, Mockito.times(1))
                .riderHasNewAssignments(userAssignment1.getUsername());

        // check if the second user was not iterated
        Mockito.verify(ordersCache, Mockito.times(0))
                .riderHasNewAssignments(userAssignment2.getUsername());

        Mockito.verify(ordersCache, Mockito.times(1))
                .assignOrder(
                        userAssignment1.getUsername(),
                        22L
                );

        Mockito.verify(ordersCache, Mockito.times(0))
                .addOrderToQueue(22L);
    }

    @Test
    void whenRiderIsBusy_itShouldBeFiltered() {
        userAssignment1.getRider().setBusy(true);

        Mockito.when(userRepository.getRidersLogged()).thenReturn(
                Arrays.asList(userAssignment1,
                        userAssignment2,
                        userAssignment3,
                        userAssignment4)
        );

        Mockito.when(ordersCache.riderHasNewAssignments(
                userAssignment2.getUsername()
        )).thenReturn(false);

        orderServiceImp.assignOrderToRider(orderAssignment);

        Mockito.verify(ordersCache, Mockito.times(1))
                .riderHasNewAssignments(userAssignment2.getUsername());

        // check if the second user was not iterated
        Mockito.verify(ordersCache, Mockito.times(0))
                .riderHasNewAssignments(userAssignment3.getUsername());

        Mockito.verify(ordersCache, Mockito.times(1))
                .assignOrder(
                        userAssignment2.getUsername(),
                        22L
                );

        Mockito.verify(ordersCache, Mockito.times(0))
                .addOrderToQueue(22L);
    }

    @Test
    void whenAllRidersAreBusy_thenAddOrderToQueue() {
        userAssignment1.getRider().setBusy(true);
        userAssignment2.getRider().setBusy(true);
        userAssignment3.getRider().setBusy(true);
        userAssignment4.getRider().setBusy(true);

        Mockito.when(userRepository.getRidersLogged()).thenReturn(
                Arrays.asList(userAssignment1,
                        userAssignment2,
                        userAssignment3,
                        userAssignment4)
        );

        Mockito.verify(ordersCache, Mockito.times(0))
                .riderHasNewAssignments(userAssignment1.getUsername());

        Mockito.verify(ordersCache, Mockito.times(0))
                .addOrderToQueue(22L);
    }
}