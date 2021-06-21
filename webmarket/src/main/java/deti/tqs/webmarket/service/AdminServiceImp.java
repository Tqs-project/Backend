package deti.tqs.webmarket.service;

import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.*;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.RiderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.*;

@Log4j2
@Service
public class AdminServiceImp implements AdminService{

    @Autowired
    private OrdersCache ordersCache;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom rand = new SecureRandom();

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

    @Override
    public List<RiderFullInfoDto> getRiders() {
        var ret = new ArrayList<RiderFullInfoDto>();

        riderRepository.findAll().forEach(
                rider -> ret.add(
                        Utils.parseRiderDto(rider)
                )
        );
        return ret;
    }

    @Override
    public TokenDto login(CustomerLoginDto loginParams) {
        var user = this.userRepository.findByUsername(
                loginParams.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(
                        "User not found with username: " + loginParams.getUsername()
                )
        );
        log.info(loginParams);
        log.info(user);

        if (loginParams.getPassword().equals(user.getPassword())) {
            var token = this.encoder.encode(String.valueOf(rand.nextDouble()));

            user.setAuthToken(token);
            this.userRepository.saveAndFlush(user);

            return new TokenDto(token, "");
        }
        return new TokenDto("", "Bad authentication parameters");
    }

    @Override
    public void logout(String username) {
        var user = this.userRepository.findByUsername(
                username).orElseThrow(
                () -> new EntityNotFoundException(
                        "User not found with username: " + username
                )
        );

        user.setAuthToken(null);
        this.userRepository.saveAndFlush(user);
    }
}
