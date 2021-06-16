package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.dto.UserDto;
import deti.tqs.webmarket.model.*;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RideRepository;
import deti.tqs.webmarket.repository.RiderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Ride;
import deti.tqs.webmarket.model.Rider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RiderServiceImpTest {

    @Mock(lenient = true)
    private RideRepository rideRepository;

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private RiderRepository riderRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock
    private OrdersCache ordersCache;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private RiderServiceImp riderService;

    private Rider riderRepo;
    private Order orderRepo;
    private Ride rideRepo;

    private RiderDto riderCreateDto;
    private RiderDto riderCreateDtoRet;

    private User user;
    private Rider rider;
    private User userWithId;
    private Rider riderFromDb;

    private TokenDto token;

    @BeforeEach
    void setUp() {
        var userForQueue = new User();
        userForQueue.setUsername("Very beautiful name");
        riderRepo = new Rider();
        riderRepo.setBusy(true);
        riderRepo.setUser(userForQueue);

        orderRepo = new Order();
        orderRepo.setId(1L);
        orderRepo.setRide(rideRepo);

        rideRepo = new Ride();
        rideRepo.setId(orderRepo.getId());
        rideRepo.setRider(riderRepo);
        rideRepo.setOrder(orderRepo);

        riderCreateDtoRet = new RiderDto(
                new UserDto(
                        "Pedro",
                        "pedrocas@gmail.com",
                        "RIDER",
                        "",
                        "935665522"
                ),
                "GH-32-8J");

        riderCreateDto = new RiderDto(
                new UserDto(
                        "Pedro",
                        "pedrocas@gmail.com",
                        "RIDER",
                        "password",
                        "935665522"
                ),
                "GH-32-8J");

        user = new User(
                riderCreateDto.getUser().getUsername(),
                riderCreateDto.getUser().getEmail(),
                "RIDER",
                riderCreateDto.getUser().getPassword() + "-encoded",
                riderCreateDto.getUser().getPhoneNumber()
        );

        userWithId = new User(
                riderCreateDto.getUser().getUsername(),
                riderCreateDto.getUser().getEmail(),
                "RIDER",
                riderCreateDto.getUser().getPassword() + "-encoded",
                riderCreateDto.getUser().getPhoneNumber()
        );
        userWithId.setId(4L);

        rider = new Rider(
                user,
                riderCreateDto.getVehiclePlate()
        );

        riderFromDb = new Rider(
                userWithId,
                riderCreateDto.getVehiclePlate()
        );
        riderFromDb.setId(userWithId.getId());

        userWithId.setRider(riderFromDb);

        token = new TokenDto("encrypted-token", "");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateOrderStatusNoOrderTest() {
        /**
         * updating a non existing order should return false
         */
        Mockito.when(rideRepository.findById(1L)).thenReturn(
                Optional.empty()
        );

        assertThat(
            riderService.updateOrderDelivered(1L)
        ).isFalse();
    }

    @Test
    void updateOrderStatusWithoutErrorsAndNoOrdersOnQueueTest() {
        Mockito.when(rideRepository.findById(rideRepo.getId())).thenReturn(
                Optional.of(rideRepo)
        );

        Mockito.when(ordersCache.queueHasOrders())
                .thenReturn(false);

        assertThat(
                riderService.updateOrderDelivered(rideRepo.getId())
        ).isTrue();

        assertThat(riderRepo.getBusy()).isFalse();

        assertThat(orderRepo.getStatus()).isEqualTo("DELIVERED");

        Mockito.verify(ordersCache, Mockito.times(1))
                .queueHasOrders();

        Mockito.verify(ordersCache, Mockito.times(0))
                .assignOrder(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
    }

    @Test
    void updateOrderStatusWithoutErrorsAndOrdersOnQueueTest() {

        Mockito.when(rideRepository.findById(rideRepo.getId())).thenReturn(
                Optional.of(rideRepo)
        );

        Mockito.when(ordersCache.queueHasOrders())
                .thenReturn(true);

        Mockito.when(ordersCache.getOrderFromQueue())
                .thenReturn(200L);

        assertThat(
                riderService.updateOrderDelivered(rideRepo.getId())
        ).isTrue();

        assertThat(riderRepo.getBusy()).isFalse();

        assertThat(orderRepo.getStatus()).isEqualTo("DELIVERED");

        Mockito.verify(ordersCache, Mockito.times(1))
                .queueHasOrders();

        Mockito.verify(ordersCache, Mockito.times(1))
                .assignOrder(riderRepo.getUser().getUsername(), 200L);
    }

    @Test
    void whenLoginIsMadeWithUsername_thenReturnTokenToTheSession() {
        Mockito.when(encoder.encode(ArgumentMatchers.anyString())).thenReturn(
                "encrypted-token"
        );

        Mockito.when(encoder.matches(riderCreateDto.getUser().getPassword(),
                riderCreateDto.getUser().getPassword() + "-encoded"))
                .thenReturn(true);

        Mockito.when(userRepository.findByUsername(riderCreateDto.getUser().getUsername()))
                .thenReturn(Optional.of(userWithId));

        assertThat(riderService.login(riderCreateDto))
                .isEqualTo(token);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(
                riderCreateDto.getUser().getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(
                riderCreateDto.getUser().getEmail()
        );
    }

    @Test
    void whenLoginIsMadeWithEmail_thenReturnTokenToTheSession() {
        Mockito.when(encoder.encode(ArgumentMatchers.anyString())).thenReturn(
                "encrypted-token"
        );

        Mockito.when(encoder.matches(riderCreateDto.getUser().getPassword(),
                riderCreateDto.getUser().getPassword() + "-encoded"))
                .thenReturn(true);

        riderCreateDto.getUser().setUsername(null);
        Mockito.when(userRepository.findByEmail(riderCreateDto.getUser().getEmail()))
                .thenReturn(Optional.of(userWithId));

        assertThat(riderService.login(riderCreateDto))
                .isEqualTo(token);

        Mockito.verify(userRepository, Mockito.times(0)).findByUsername(
                riderCreateDto.getUser().getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(
                riderCreateDto.getUser().getEmail()
        );
    }

    @Test
    void whenLoginAndNoUsernameOrEmailIsProvided_thenReturnEmptyToken() {
        riderCreateDto.getUser().setUsername(null);
        riderCreateDto.getUser().setEmail(null);
        var tokenRet = new TokenDto(
                "", "Please provide username or email for authentication"
        );

        assertThat(riderService.login(riderCreateDto))
                .isEqualTo(tokenRet);

        Mockito.verify(userRepository, Mockito.times(0)).findByUsername(
                riderCreateDto.getUser().getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(
                riderCreateDto.getUser().getEmail()
        );
    }

    @Test
    void whenLoginWithInvalidUsernameOrEmail_thenReturnEmptyToken() {
        var tokenRet = new TokenDto(
                "", "Bad authentication parameters"
        );

        Mockito.when(userRepository.findByUsername(riderCreateDto.getUser().getUsername()))
                .thenReturn(Optional.empty());

        assertThat(riderService.login(riderCreateDto))
                .isEqualTo(tokenRet);
    }

    @Test
    void whenLoginIsMadeWithInvalidPassword_thenReturnEmptyToken() {
        var invalidPassword = "im-the-exterminator";
        var tokenRet = new TokenDto(
                "", "Bad authentication parameters"
        );

        var correctPassword = riderCreateDto.getUser().getPassword();
        riderCreateDto.getUser().setPassword(invalidPassword);
        Mockito.when(encoder.matches(riderCreateDto.getUser().getPassword(),
                correctPassword + "-encoded"))
                .thenReturn(false);

        riderCreateDto.getUser().setUsername(null);
        Mockito.when(userRepository.findByEmail(riderCreateDto.getUser().getEmail()))
                .thenReturn(Optional.of(userWithId));

        assertThat(riderService.login(riderCreateDto))
                .isEqualTo(tokenRet);

        Mockito.verify(encoder, Mockito.times(1)).matches(
                riderCreateDto.getUser().getPassword(), correctPassword + "-encoded"
        );
    }

    @Test
    void riderHasNewAssignmentTest() {
        var rider = "rider1";
        Mockito.when(ordersCache.riderHasNewAssignments(rider)).thenReturn(
                false
        );

        assertThat(riderService.riderHasNewAssignment(rider))
                .isFalse();
    }

    @Test
    void retrieveOrderAssignedTest() {
        var customer = new Customer(
                userWithId,
                "hell",
                "a very, but very good restaurant",
                "restaurant",
                "asdfasdasd"
        );
        var order = new Order(
            "MB",
                20.0,
                customer,
                customer.getAddress()
        );
        order.setId(222L);
        order.setStatus("WAITING");

        var orderDtoAssignOrder = new OrderDto(
                order.getId(),
                order.getOrderTimestamp(),
                order.getPaymentType(),
                order.getStatus(),
                order.getCost(),
                order.getLocation(),
                order.getCustomer().getId(),
                order.getCustomer().getUser().getUsername(),
                null
        );

        var rider = "Torres";

        Mockito.when(ordersCache.retrieveAssignedOrder(rider))
                .thenReturn(order.getId());

        Mockito.when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        assertThat(
                riderService.retrieveOrderAssigned(rider)
        ).isEqualTo(orderDtoAssignOrder);

        Mockito.verify(ordersCache, Mockito.times(1))
                .retrieveAssignedOrder(rider);

        Mockito.verify(orderRepository, Mockito.times(1))
                .findById(order.getId());
    }


}