package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("")
    public Order createOrder(@Valid @RequestBody OrderDto orderDto) throws Exception {
        return orderService.createOrder(orderDto);
    }

    @GetMapping("")
    public List<Order> getOrders(@RequestParam int id){
        Customer customer = customerRepository.findById(id);
        
        return orderService.getAllOrdersByCustomer(customer);
    }
}
