package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@RestController
@RequestMapping("/api/customer/")
@Validated
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("create")
    @PostAuthorize("hasAuthority('CUSTOMER')")
    public CustomerDto createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return this.customerService.createCustomer(customerDto);
    }

    // @PreAuthorize("!hasAuthority('USER')")

}
