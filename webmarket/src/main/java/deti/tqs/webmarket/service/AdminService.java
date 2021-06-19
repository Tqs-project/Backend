package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.RiderFullInfoDto;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface AdminService {
    Map<String, Long> getCurrentAssignments();
    Queue<Long> getWaitingOrdersAssignment();
    void resetOrdersCache();

    List<CustomerDto> getCustomers();
    List<OrderDto> getOrders();
    List<RiderFullInfoDto> getRiders();
}
