package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public Order createOrder(OrderDto orderDto) throws Exception {
        Customer customer = new ModelMapper().map(orderDto.getCustomer(), Customer.class);
        Order order = new Order(orderDto.getPaymentType(), orderDto.getCost(), customer, orderDto.getLocation());
        return repository.save(order);
    }

    public List<Order> getAllOrdersByCustomer(Customer customer) {
        return repository.findOrdersByCustomer(customer);
    }
}
