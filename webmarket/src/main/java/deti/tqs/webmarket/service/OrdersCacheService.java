package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersCacheService {

    @Autowired
    private OrdersCache ordersCache;

    public void assignOrder(String usernameRider, Long orderId) {
        ordersCache.assignOrder(usernameRider, orderId);
    }

    public void removeOrderAssignment(String usernameRider) {
        ordersCache.removeOrderAssignment(usernameRider);
    }

    public boolean riderHasNewAssignments(String usernameRider) {
        return ordersCache.riderHasNewAssignments(usernameRider);
    }
}
