package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByOrderTimestampAfter(Timestamp timestamp);
    List<Order> findOrdersByOrderTimestampBefore(Timestamp timestamp);
    List<Order> findOrdersByCustomer(Customer customer);
    List<Order> findOrdersByCustomer_User_Username(String username);
}
