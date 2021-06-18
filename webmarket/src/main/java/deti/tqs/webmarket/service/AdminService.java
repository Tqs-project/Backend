package deti.tqs.webmarket.service;

import java.util.Map;
import java.util.Queue;

public interface AdminService {
    Map getCurrentAssignments();
    Queue getWaitingOrdersAssignment();
    void resetOrdersCache();
}
