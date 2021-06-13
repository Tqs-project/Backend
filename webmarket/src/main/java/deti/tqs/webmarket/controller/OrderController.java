package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.OrderDto;
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

    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        log.info("Saving order " + orderDto.getLocation() + ".");
        return new ResponseEntity<>(this.orderService.createOrder(orderDto),
                HttpStatus.CREATED);
    }

    /*@PutMapping()
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderDto orderDto) {
        log.info(String.format("Updating order %s.", orderDto.getLocation()));
        return new ResponseEntity<>(this.orderService.createOrder(orderDto),
                HttpStatus.OK);
    }*/
}
