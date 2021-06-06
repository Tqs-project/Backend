package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/customer/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping()
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        log.info("Saving customer " + customerDto.getUsername() + ".");
        return new ResponseEntity<>(this.customerService.createCustomer(customerDto),
                HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerDto customerDto) {
        log.info(String.format("Updating customer %s.", customerDto.getUsername()));
        return new ResponseEntity<>(this.customerService.updateCustomer(customerDto),
                HttpStatus.OK);
    }

}
