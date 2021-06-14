package deti.tqs.webmarket.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrdersCache {
    /**
     * This class with have the purpose of store all the assignments made for orders
     * Waht it means, is that a rider will check frequently if a new orders was assigned to him
     * using this in memory cache
     *
     * so, i can make a request to the endpoint, and if a order was assigned to him, he will receive the
     * specifications of the order
     * And after that, he can accept or decline the order
     */
    private HashMap<String, Long> ordersCache;

    public OrdersCache() {
        ordersCache = new HashMap<>();
    }

    public void assignOrder(String usernameRider, Long orderId) {
        this.ordersCache.put(usernameRider, orderId);
    }

    public void removeOrderAssignment(String usernameRider) {
        this.ordersCache.remove(usernameRider);
    }

    public boolean riderHasNewAssignments(String usernameRider) {
        return this.ordersCache.containsKey(usernameRider);
    }
}
