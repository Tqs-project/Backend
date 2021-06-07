package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import javassist.tools.web.BadHttpRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Log4j2
@Service
public class CustomerServiceImp implements CustomerService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        var user = new User(
            customerDto.getUsername(),
                customerDto.getEmail(),
                "CUSTOMER",
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
        var ret = this.customerRepository.save(customer);
        return Utils.parseCustomerDto(ret);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        var user = this.userRepository.findByUsername(customerDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("No user with username " + customerDto.getUsername() + ".")
        );
        var customer = user.getCustomer();

        user.setEmail(customerDto.getEmail());
        user.setPassword(customerDto.getPassword());
        user.setPhoneNumber(customerDto.getPhoneNumber());

        customer.setAddress(customerDto.getAddress());
        customer.setDescription(customerDto.getDescription());
        customer.setImageUrl(customerDto.getImageUrl());
        customer.setTypeOfService(customerDto.getTypeOfService());
        customer.setIban(customerDto.getIban());

        this.userRepository.save(user);
        var ret = this.customerRepository.save(customer);
        return Utils.parseCustomerDto(ret);
    }

    @Override
    public TokenDto login(CustomerDto customerDto) {
        User user;
        if (customerDto.getUsername() != null) {
            user = this.userRepository.findByUsername(customerDto.getUsername()).orElseThrow(
                    () -> new EntityNotFoundException("No user with username " + customerDto.getUsername() + ".")
            );
        } else if (customerDto.getEmail() != null) {
            user = this.userRepository.findByEmail(customerDto.getEmail()).orElseThrow(
                    () -> new EntityNotFoundException("No user with the email specified.")
            );
        } else {
            throw new RuntimeException("Please provide username or email for authentication");
        }

        if (customerDto.getPassword().equals(user.getPassword())) {
            return new TokenDto("this-is-the-token", "");
        }
        return new TokenDto("", "Bad authentication parameters");
    }
}
