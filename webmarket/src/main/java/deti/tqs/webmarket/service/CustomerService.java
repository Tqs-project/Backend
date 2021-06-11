package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;

import deti.tqs.webmarket.model.Customer;

import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Customer createCustomer(CustomerDto customerDto) throws Exception {
        User user = new ModelMapper().map(customerDto.getUser(), User.class);
        Customer customer = new Customer(user, customerDto.getAddress(), customerDto.getDescription(), customerDto.getTypeOfService(), customerDto.getIban());
        return repository.save(customer);
    }

    //public List<Order> getAllOrdersByCustomer(Customer customer) {
    //    return repository.findOrdersByCustomer(customer);
    //}

}
