package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findAllByRole(String role);

    // check if the user is logged
    @Query(
            value = "SELECT * FROM users WHERE auth_token IS NOT NULL AND role = 'RIDER'",
            nativeQuery = true
    )
    List<User> getRidersLogged();
    
}
