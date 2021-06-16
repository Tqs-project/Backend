package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.OrderCreateDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(@RequestHeader String username,
                                            @RequestHeader String idToken,
                                            @RequestBody OrderCreateDto orderDto) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>(new OrderDto(), HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>(new OrderDto(), HttpStatus.UNAUTHORIZED);

        log.info("Saving order " + orderDto.getLocation() + ".");
        var order = new OrderDto();
        order.setUsername(orderDto.getUsername());
        order.setPaymentType(orderDto.getPaymentType());
        order.setCost(orderDto.getCost());
        order.setLocation(orderDto.getLocation());
        return new ResponseEntity<>(this.orderService.createOrder(order),
                HttpStatus.CREATED);
    }
}
