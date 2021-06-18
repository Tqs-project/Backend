package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.LocationDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;

public interface RiderService {
    RiderDto registerRider(RiderDto riderDto);
    Rider updateRider(RiderDto riderDto);
    TokenDto login(RiderDto riderDto);
    boolean updateOrderDelivered(Long orderId);

    boolean riderHasNewAssignment(String username);
    OrderDto retrieveOrderAssigned(String username);

    void riderAcceptsAssignment(String username);
    void riderDeclinesAssignment(String username);

    void updateRiderLocation(Rider rider, LocationDto location);
}
