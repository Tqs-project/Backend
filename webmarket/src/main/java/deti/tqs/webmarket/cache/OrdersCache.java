package deti.tqs.webmarket.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

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
    private Queue<Long> waitingOrdersQueue;

    public OrdersCache() {
        ordersCache = new HashMap<>();
        waitingOrdersQueue = new LinkedList<>();
    }

    public void assignOrder(String usernameRider, Long orderId) {
        this.ordersCache.put(usernameRider, orderId);
    }

    public Long retrieveAssignedOrder(String username) {
        return this.ordersCache.get(username);
    }

    public void removeOrderAssignment(String usernameRider) {
        this.ordersCache.remove(usernameRider);
    }

    public boolean riderHasNewAssignments(String usernameRider) {
        return this.ordersCache.containsKey(usernameRider);
    }

    public void addOrderToQueue(Long orderId) { this.waitingOrdersQueue.add(orderId); }

    public Long getOrderFromQueue() { return this.waitingOrdersQueue.remove(); }

    public boolean queueHasOrders() {
        return !this.waitingOrdersQueue.isEmpty();
    }
}
