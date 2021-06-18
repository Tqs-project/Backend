package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;

@Service
public class AdminServiceImp implements AdminService{

    @Autowired
    private OrdersCache ordersCache;

    @Override
    public Map getCurrentAssignments() {
        return this.ordersCache.getAssignments();
    }

    @Override
    public Queue getWaitingOrdersAssignment() {
        return this.ordersCache.getWaitingAssignmentOrders();
    }

    @Override
    public void resetOrdersCache() {
        this.ordersCache.deleteAllOrders();
    }
}
