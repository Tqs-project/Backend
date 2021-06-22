package deti.tqs.webmarket.service;

import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.DistanceMatrixRow;
import deti.tqs.webmarket.api.DistanceAPI;
import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.PriceEstimationDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class CustomerServiceImp implements CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DistanceAPI distanceAPI;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom rand = new SecureRandom();

    private static final double COST_PER_METER = 0.00067;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        var user = new User(
            customerDto.getUsername(),
                customerDto.getEmail(),
                "CUSTOMER",
                encoder.encode(customerDto.getPassword()),
                customerDto.getPhoneNumber()
        );
        var customer = new Customer(
                user,
                customerDto.getAddress(),
                customerDto.getDescription(),
                customerDto.getTypeOfService(),
                customerDto.getIban()
        );

        this.userRepository.save(user);
        var ret = this.customerRepository.saveAndFlush(customer);
        return Utils.parseCustomerDto(ret);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        var user = this.userRepository.findByUsername(customerDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("No user with username " + customerDto.getUsername() + ".")
        );
        var customer = user.getCustomer();

        user.setEmail(customerDto.getEmail());
        user.setPassword(encoder.encode(customerDto.getPassword()));
        user.setPhoneNumber(customerDto.getPhoneNumber());

        customer.setAddress(customerDto.getAddress());
        customer.setDescription(customerDto.getDescription());
        customer.setImageUrl(customerDto.getImageUrl());
        customer.setTypeOfService(customerDto.getTypeOfService());
        customer.setIban(customerDto.getIban());

        this.userRepository.saveAndFlush(user);
        var ret = this.customerRepository.saveAndFlush(customer);
        return Utils.parseCustomerDto(ret);
    }

    @Override
    public TokenDto login(CustomerDto customerDto) {
        Optional<User> optUser;
        if (customerDto.getUsername() != null) {
            optUser = this.userRepository.findByUsername(customerDto.getUsername());
        } else if (customerDto.getEmail() != null) {
            optUser = this.userRepository.findByEmail(customerDto.getEmail());
        } else {
            return new TokenDto("", "Please provide username or email for authentication");
        }

        if (optUser.isEmpty()) {
            log.debug("No user found");
            return new TokenDto("", "Bad authentication parameters");
        }

        var user = optUser.get();
        if (this.encoder.matches(customerDto.getPassword(), user.getPassword())) {
            var token = this.encoder.encode(String.valueOf(rand.nextDouble()));

            user.setAuthToken(token);
            this.userRepository.saveAndFlush(user);

            return new TokenDto(token, "");
        }
        return new TokenDto("", "Bad authentication parameters");
    }

    @Override
    public boolean orderBelongsToCustomer(Customer customer, Long orderId) {
        for (Order order : customer.getOrders())
            if (order.getId().equals(orderId))
                return true;

        return false;
    }

    @Override
    public OrderDto getCustomerOrder(Long orderId) {
        return Utils.parseOrderDto(
                this.orderRepository.findById(orderId).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Order not found with id: " + orderId
                        )
                )
        );
    }

    @Override
    public List<OrderDto> getAllCustomerOrders(String username) {
        var orders = orderRepository.findOrdersByCustomer_User_Username(username);

        var ret = new ArrayList<OrderDto>();

        orders.forEach(
                order -> ret.add(Utils.parseOrderDto(order))
        );
        return ret;
    }

    @Override
    public PriceEstimationDto getPriceForDelivery(Long customerId, String destination) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer not found with id: " + customerId)
        );

        var response = distanceAPI.getDistance(
                new String[] { customer.getAddress() },
                new String[] { destination }
        );

        if (response.rows[0].elements[0].status.equals(DistanceMatrixElementStatus.NOT_FOUND))
            return new PriceEstimationDto();

        var rideInfo = response.rows[0].elements[0];

        var cost = Precision.round(COST_PER_METER * rideInfo.distance.inMeters, 2);
        return new PriceEstimationDto(
                response.originAddresses[0],
                response.destinationAddresses[0],
                rideInfo.duration.humanReadable,
                rideInfo.distance.inMeters,
                rideInfo.distance.humanReadable,
                cost
        );
    }
}
