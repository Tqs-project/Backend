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

    @PostMapping
    public Order createOrder(@Valid @RequestBody OrderDto orderDto) throws Exception {
        System.out.println(orderDto);

        Order o =orderService.createOrder(orderDto);
        System.out.println(o.getCost() + " --- " + o.getId() + " --- " + o.getLocation() + " --- " + o.getOrderTimestamp());
        return o;
    }
    /*
    {
    "paymentType": "MB",
    "cost": 100,
    "customer": {
        "user": {
            "username": "drinkUp",
            "email": "drinkup@gmail.com",
            "role": "CUSTOMER",
            "password": "pass",
            "phoneNumber": "+351 938736"
        },
        "address": "Aveiro",
        "description": "drink's store",
        "typeOfService": "Drinks",
        "iban": "PT2383288"
    },
    "location": "Rua da Anadia",
    }
     */

    /*
    @GetMapping
    public List<Order> getOrders(@RequestParam int id){
        Customer customer = customerRepository.findById(id);
        
        return orderService.getAllOrdersByCustomer(customer);
    }*/

    @GetMapping
    public String index() {
        return "test";
    }
}
