package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.OrderRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

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

    @Override
    public OrderDto createOrder(OrderDto orderDto){

        var user = this.userRepository.findByUsername(orderDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("No user with username " + orderDto.getUsername() + ".")
        );

        var customer = user.getCustomer();

        var order = new Order(
               orderDto.getPaymentType(),
               orderDto.getCost(),
               customer,
               orderDto.getLocation()
        );
        customer.getOrders().add(order);

        var ret = this.orderRepository.save(order);
        this.customerRepository.save(customer);
        return Utils.parseOrderDto(ret);
    }


}
