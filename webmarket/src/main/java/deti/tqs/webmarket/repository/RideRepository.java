package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Ride;
import deti.tqs.webmarket.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findRidesByDestination(String destination);
    List<Ride> findRidesByRider(Rider rider);
}
