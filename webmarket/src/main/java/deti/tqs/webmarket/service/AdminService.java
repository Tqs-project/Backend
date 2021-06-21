package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.*;

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

    TokenDto login(CustomerLoginDto loginParams);
    void logout(String username);
}
