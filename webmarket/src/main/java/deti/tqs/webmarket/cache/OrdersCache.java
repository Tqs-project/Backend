package deti.tqs.webmarket.cache;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
@Transactional
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
    private Map<String, Long> ordersAssigned;
    private Queue<Long> waitingOrdersQueue;

    public OrdersCache() {
        ordersAssigned = new HashMap<>();
        waitingOrdersQueue = new LinkedList<>();
    }

    public Map<String, Long> getAssignments() { return this.ordersAssigned; }
    public Queue<Long> getWaitingAssignmentOrders() { return this.waitingOrdersQueue; }

    public void assignOrder(String usernameRider, Long orderId) {
        this.ordersAssigned.put(usernameRider, orderId);
    }

    public Long retrieveAssignedOrder(String username) {
        return this.ordersAssigned.get(username);
    }

    public void removeOrderAssignment(String usernameRider) {
        this.ordersAssigned.remove(usernameRider);
    }

    public boolean riderHasNewAssignments(String usernameRider) {
        return this.ordersAssigned.containsKey(usernameRider);
    }

    public void addOrderToQueue(Long orderId) { this.waitingOrdersQueue.add(orderId); }

    public Long getOrderFromQueue() { return this.waitingOrdersQueue.remove(); }

    public boolean queueHasOrders() {
        return !this.waitingOrdersQueue.isEmpty();
    }

    public void deleteAllOrders() {
        this.ordersAssigned.clear();
        this.waitingOrdersQueue.clear();
    }
}
