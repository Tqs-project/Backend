package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    List<Rider> findRidersByBusyEquals(Boolean busy);
    Optional<Rider> findByUser_Email(String email);
    Optional<Rider> findByUser_Username(String username);
    Boolean existsByUser_Email(String email);
}
