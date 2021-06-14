package deti.tqs.webmarket.cache;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrdersCacheTest {

    private OrdersCache cache;

    @BeforeEach
    void setUp() {
        cache = new OrdersCache();
    }

    @Test
    void ifAOrderWasAssignedToARider_whenHeChecksIfNewOrdersWhereAssignedToHim_thenHeShouldReceiveTheConfirmation() {
        var rider = "Ghost Rider";
        cache.assignOrder(rider, 1L);

        assertThat(
                cache.riderHasNewAssignments(rider)
        ).isTrue();

        // Lets pretend he declined the order
        cache.removeOrderAssignment(rider);

        assertThat(
                cache.riderHasNewAssignments(rider)
        ).isFalse();

    }

    @Test
    void testQueue() {
        cache.addOrderToQueue(1L);
        cache.addOrderToQueue(2L);

        Assertions.assertThat(
                cache.getOrderFromQueue()
        ).isEqualTo(1L);
    }
}