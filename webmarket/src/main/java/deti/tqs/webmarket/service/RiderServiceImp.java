package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.UserDto;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RideRepository;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.RiderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Log4j2
@Service
@Transactional
public class RiderServiceImp implements RiderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository repository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrdersCache ordersCache;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom rand = new SecureRandom();

    public RiderDto registerRider(RiderDto riderDto) throws Exception {
        if (repository.existsByUser_Email(riderDto.getUser().getEmail())) {
            throw new Exception("That email is already in use!");
        } else {
            var user = new User(
                    riderDto.getUser().getUsername(),
                    riderDto.getUser().getEmail(),
                    "RIDER",
                    encoder.encode(riderDto.getUser().getPassword()),
                    riderDto.getUser().getPhoneNumber()
            );
            var rider = new Rider(user, riderDto.getVehiclePlate());
            user.setRider(rider);
            repository.saveAndFlush(rider);
            userRepository.saveAndFlush(user);

            UserDto responseUser = riderDto.getUser();
            responseUser.setPassword("");
            responseUser.setRole("RIDER");
            return new RiderDto(responseUser, riderDto.getVehiclePlate());
        }
    }

    @Override
    public Rider updateRider(RiderDto riderDto) {
        return null;
    }

    public TokenDto login(RiderDto riderDto){
        Optional<User> optUser;
        if (riderDto.getUser().getUsername() != null) {
            optUser = this.userRepository.findByUsername(riderDto.getUser().getUsername());
        } else if (riderDto.getUser().getEmail() != null) {
            optUser = this.userRepository.findByEmail(riderDto.getUser().getEmail());
        } else {
            return new TokenDto("", "Please provide username or email for authentication");
        }

        if (optUser.isEmpty()) {
            log.debug("No user found");
            return new TokenDto("", "Bad authentication parameters");
        }

        var user = optUser.get();
        if (this.encoder.matches(riderDto.getUser().getPassword(), user.getPassword())) {
            var token = this.encoder.encode(String.valueOf(rand.nextDouble()));

            user.setAuthToken(token);
            this.userRepository.saveAndFlush(user);

            return new TokenDto(token, "");
        }
        return new TokenDto("", "Bad authentication parameters");
    }

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

        orderRepository.saveAndFlush(order);
        rideRepository.saveAndFlush(ride);
        repository.saveAndFlush(rider);

        /**
         * now this rider is not busy
         * so he can grab one of the orders on the queue
         */
        if (!ordersCache.queueHasOrders())
            return true;

        // it means that the queue has orders
        // we can just assign the first order stored to the rider
        ordersCache.assignOrder(rider.getUser().getUsername(),
                ordersCache.getOrderFromQueue());
        return true;
    }

    public Optional<Rider> getRiderByEmail(String email) {
        return repository.findByUser_Email(email);
    }

    public List<Rider> getAllRiders() {
        return repository.findAll();
    }

    public boolean riderHasNewAssignment(String username) {
        return ordersCache.riderHasNewAssignments(username);
    }

    public OrderDto retrieveOrderAssigned(String username) {
        var orderId = ordersCache.retrieveAssignedOrder(username);

        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order with id " + orderId + " doesn't exist.")
        );

        return Utils.parseOrderDto(order);
    }

}
