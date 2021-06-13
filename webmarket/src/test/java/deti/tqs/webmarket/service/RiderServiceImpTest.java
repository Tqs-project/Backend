package deti.tqs.webmarket.service;

import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Ride;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RideRepository;
import deti.tqs.webmarket.repository.RiderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private RiderServiceImp riderService;

    private Rider riderRepo;
    private Order orderRepo;
    private Ride rideRepo;

    @BeforeEach
    void setUp() {
        riderRepo = new Rider();
        riderRepo.setBusy(true);

        orderRepo = new Order();
        orderRepo.setId(1L);
        orderRepo.setRide(rideRepo);

        rideRepo = new Ride();
        rideRepo.setId(orderRepo.getId());
        rideRepo.setRider(riderRepo);
        rideRepo.setOrder(orderRepo);
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
    void updateOrderStatusWithoutErrorsTest() {
        Mockito.when(rideRepository.findById(rideRepo.getId())).thenReturn(
                Optional.of(rideRepo)
        );

        assertThat(
                riderService.updateOrderDelivered(rideRepo.getId())
        ).isTrue();

        assertThat(riderRepo.getBusy()).isFalse();

        assertThat(orderRepo.getStatus()).isEqualTo("DELIVERED");
    }
}