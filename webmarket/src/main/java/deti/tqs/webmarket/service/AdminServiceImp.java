package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Service
public class AdminServiceImp implements AdminService{

    @Autowired
    private OrdersCache ordersCache;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Map<String, Long> getCurrentAssignments() {
        return this.ordersCache.getAssignments();
    }

    @Override
    public Queue<Long> getWaitingOrdersAssignment() {
        return this.ordersCache.getWaitingAssignmentOrders();
    }

    @Override
    public void resetOrdersCache() {
        this.ordersCache.deleteAllOrders();
    }

    @Override
    public List<CustomerDto> getCustomers() {
        var ret = new ArrayList<CustomerDto>();

        customerRepository.findAll().forEach(
                customer -> ret.add(
                        Utils.parseCustomerDto(customer)
                )
        );
        return ret;
    }

    @Override
    public List<OrderDto> getOrders() {
        var ret = new ArrayList<OrderDto>();

        orderRepository.findAll().forEach(
                order -> ret.add(
                        Utils.parseOrderDto(order)
                )
        );
        return ret;
    }
}
