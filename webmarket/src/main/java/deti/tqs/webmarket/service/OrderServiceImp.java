package deti.tqs.webmarket.service;

import deti.tqs.webmarket.api.DistanceAPI;
import deti.tqs.webmarket.cache.OrdersCache;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersCache ordersCache;

    @Autowired
    private CustomerService customerService;

    @Override
    public OrderDto createOrder(OrderDto orderDto){
        var user = this.userRepository.findByUsername(orderDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("No user with username " + orderDto.getUsername() + ".")
        );

        var customer = user.getCustomer();

        var price = customerService.getPriceForDelivery(user.getId(), orderDto.getLocation()).getDeliveryPrice();

        if (price == null)
            return new OrderDto();

        var order = new Order(
               orderDto.getPaymentType(),
               price,
               customer,
               orderDto.getLocation()
        );
        customer.getOrders().add(order);

        var ret = this.orderRepository.save(order);
        this.customerRepository.save(customer);

        /**
         * now we have to assign the order to a rider
         */
        assignOrderToRider(ret);

        return Utils.parseOrderDto(ret);
    }

    protected void assignOrderToRider(Order order) {
        // first we have to get all the riders available
        var ridersLogged = userRepository.getRidersLogged();

        // next we have to filter does that are currently not busy
        var ridersAvailable = ridersLogged.stream().filter((
                user -> !user.getRider().getBusy()
                )).collect(Collectors.toList());

        // and finally, we can pre-assign one rider to the order
        // pre-assign, because he can decline the order
        var assigned = false;
        for (User user : ridersAvailable) {
            if (!ordersCache.riderHasNewAssignments(user.getUsername())) {
                ordersCache.assignOrder(user.getUsername(), order.getId());
                log.info("Order with id " + order.getId() + " was assigned to " + user.getUsername());
                assigned = true;
                break;
            }
        }

        /**
         * if the order was not assigned to any of the riders
         * it means that they are all busy
         * or with a pre-assignment done
         *
         * so, we have to store this order
         */
        if (!assigned)
            ordersCache.addOrderToQueue(order.getId());
    }
}

