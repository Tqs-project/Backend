package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.*;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.CustomerService;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerService customerService;

    @PostMapping()
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto customerDto) {
        log.info("Saving customer " + customerDto.getUsername() + ".");
        var customer = new CustomerDto(
                null,
                customerDto.getUsername(),
                customerDto.getEmail(),
                null,
                customerDto.getPassword(),
                customerDto.getPhoneNumber(),
                customerDto.getAddress(),
                customerDto.getDescription(),
                customerDto.getImageUrl(),
                customerDto.getTypeOfService(),
                customerDto.getIban(),
                null,
                null
        );
        return new ResponseEntity<>(this.customerService.createCustomer(customer),
            HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<CustomerDto> updateCustomer(@RequestHeader String username,
            @RequestHeader String idToken,
            @RequestBody CustomerCreateDto customerDto) {

        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>(new CustomerDto(), HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>(new CustomerDto(), HttpStatus.UNAUTHORIZED);

        log.info(String.format("Updating customer %s.", customerDto.getUsername()));
        var customer = new CustomerDto(
                null,
                customerDto.getUsername(),
                customerDto.getEmail(),
                null,
                customerDto.getPassword(),
                customerDto.getPhoneNumber(),
                customerDto.getAddress(),
                customerDto.getDescription(),
                customerDto.getImageUrl(),
                customerDto.getTypeOfService(),
                customerDto.getIban(),
                null,
                null
        );
        return new ResponseEntity<>(this.customerService.updateCustomer(customer),
                HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<CustomerDto> getCustomerInformation(@RequestHeader String username,
                                                              @RequestHeader String idToken) {

        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>(new CustomerDto(), HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>(new CustomerDto(), HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                Utils.parseCustomerDto(user.get().getCustomer()),
                HttpStatus.OK
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenDto> login(@RequestBody CustomerLoginDto customerDto) {
        log.info("Logging in user");

        var customer = new CustomerDto();
        customer.setUsername(customerDto.getUsername());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());

        var response = this.customerService.login(customer);

        if (response.isEmpty())
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@RequestHeader String username,
                                             @RequestHeader String idToken,
                                             @PathVariable Long id) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>(new OrderDto(), HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>(new OrderDto(), HttpStatus.UNAUTHORIZED);

        // check if the order is from the customer specified
        if (!this.customerService.orderBelongsToCustomer(user.get().getCustomer(), id))
            return new ResponseEntity<>(new OrderDto(), HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(this.customerService.getCustomerOrder(id),
                HttpStatus.OK);
    }

    // TODO return orders of customer
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getCustomerOrders(@RequestHeader String username,
                                                            @RequestHeader String idToken) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.customerService.getAllCustomerOrders(username),
                HttpStatus.OK
        );
    }
}
