package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
