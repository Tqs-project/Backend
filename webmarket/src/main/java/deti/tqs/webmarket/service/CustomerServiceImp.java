package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.Optional;

@Log4j2
@Service
public class CustomerServiceImp implements CustomerService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom rand = new SecureRandom();

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        var user = new User(
            customerDto.getUsername(),
                customerDto.getEmail(),
                "CUSTOMER",
                encoder.encode(customerDto.getPassword()),
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
        user.setPassword(encoder.encode(customerDto.getPassword()));
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
        Optional<User> optUser;
        if (customerDto.getUsername() != null) {
            optUser = this.userRepository.findByUsername(customerDto.getUsername());
        } else if (customerDto.getEmail() != null) {
            optUser = this.userRepository.findByEmail(customerDto.getEmail());
        } else {
            return new TokenDto("", "Please provide username or email for authentication");
        }

        if (optUser.isEmpty()) {
            log.debug("No user found");
            return new TokenDto("", "Bad authentication parameters");
        }

        var user = optUser.get();
        if (this.encoder.matches(customerDto.getPassword(), user.getPassword())) {
            var token = this.encoder.encode(String.valueOf(rand.nextDouble()));

            var customer = user.getCustomer();
            customer.setAuthToken(token);
            this.customerRepository.save(customer);

            return new TokenDto(token, "");
        }
        return new TokenDto("", "Bad authentication parameters");
    }

}
