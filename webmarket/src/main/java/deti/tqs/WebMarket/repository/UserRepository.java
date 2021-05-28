package deti.tqs.WebMarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import deti.tqs.WebMarket.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
