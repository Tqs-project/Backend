package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(CustomerDto customerDto);
    TokenDto login(CustomerDto customerDto);

    boolean orderBelongsToCustomer(Customer customer, Long orderId);
    OrderDto getCustomerOrder(Long orderId);
}
