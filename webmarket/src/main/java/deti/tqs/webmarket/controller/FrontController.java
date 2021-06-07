package deti.tqs.webmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.UserRepository;

@RestController
@RequestMapping("/")
public class FrontController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public String index() {
        return "Hello!! This is an API... so... sorry but in this site you won't see pretty CSS :(";
    }

    @PostMapping("api/customer")
    public Customer createCustomer(@org.springframework.web.bind.annotation.RequestBody CustomerDto customerDto) {
        var user = new User(
            customerDto.getUsername(),
            customerDto.getEmail(),
            customerDto.getRole(),
            customerDto.getPassword(),
            customerDto.getPhoneNumber()
        );

        var customer = new Customer(
            user,
            customerDto.getAddress(),
            customerDto.getDescription(),
            customerDto.getTypeOfService(),
            customerDto.getIban()
        );

        this.userRepository.save(user);
        return this.customerRepository.save(customer);
    }
}
