package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Ride;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class RideRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();
    }

    @Test
    void test() {
        var ride = new Ride(
                "olá",
                "bro",
                "aquele mano"
        );

        this.entityManager.persistAndFlush(ride);

        Assertions.assertThat(
                this.rideRepository.findById(ride.getId()).get()
        ).isEqualTo(ride);
    }
}