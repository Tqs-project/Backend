package deti.tqs.webmarket.service;

import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RideRepository;
import deti.tqs.webmarket.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class RiderServiceImp implements RiderService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Override
    public boolean updateOrderDelivered(Long rideId) {
        /**
         * the order must exist, check that on controller
         * if the order is updated to DELIVERED
         * then the timestampEnd of the ride should be initialized
         * and obviously, the status of the order should be updated
         *
         * the rider now passes to not busy
         */
        var optRide = rideRepository.findById(rideId);
        if (optRide.isEmpty())
            return false;

        var ride = optRide.get();
        var order = ride.getOrder();

        order.setStatus("DELIVERED");
        ride.setTimestampEnd(new Timestamp(System.currentTimeMillis()));

        var rider = ride.getRider();
        rider.setBusy(false);

        orderRepository.save(order);
        rideRepository.save(ride);
        riderRepository.save(rider);
        return true;
    }
}
