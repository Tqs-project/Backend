package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUser_Email(String email);
}
