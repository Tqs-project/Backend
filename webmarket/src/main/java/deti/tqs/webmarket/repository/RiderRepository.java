package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    List<Rider> findRidersByBusyEquals(Boolean busy);
    List<Rider> findByEmail(String email);
}
