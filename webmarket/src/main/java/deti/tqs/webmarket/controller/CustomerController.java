package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody CustomerDto customerDto) throws Exception {
        System.out.println(customerDto);

        Customer c =customerService.createCustomer(customerDto);
        System.out.println(c.getId() + c.getAddress() + " --- " + c.getIban() + " --- " + c.getTypeOfService() + " --- " + c.getDescription());
        return c;
    }

    @GetMapping
    public Customer getCustomers(@RequestParam int id){
        Customer customer = customerRepository.findById(id);
        System.out.println(customer.getId());
        return  customer;
    }
}
