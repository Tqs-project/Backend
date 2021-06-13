package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerCreateDto;
import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.CustomerLoginDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

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
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerCreateDto customerDto) {
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

    @PostMapping("/signin")
    public ResponseEntity<TokenDto> login(@RequestBody CustomerLoginDto customerDto) {
        log.info("Logging in user");

        var customer = new CustomerDto();
        customer.setUsername(customerDto.getUsername());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(customer.getPassword());

        var response = this.customerService.login(customer);

        if (response.isEmpty())
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
