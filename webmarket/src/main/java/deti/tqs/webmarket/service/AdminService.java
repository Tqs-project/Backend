package deti.tqs.webmarket.service;

import java.util.Map;
import java.util.Queue;

public interface AdminService {
    Map<String, Long> getCurrentAssignments();
    Queue<Long> getWaitingOrdersAssignment();
    void resetOrdersCache();
}
