package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Comment;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class RiderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RiderRepository riderRepository;

    private User user1;
    private User user2;
    private Rider rider;
    private Customer customer;
    private Comment comment;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.user1 = new User(
                "Urlando",
                "urlando@gmail.com",
                "CUSTOMER",
                "password",
                "93555555"
        );

        this.user2 = new User(
                "Jorge",
                "jorge@gmail.com",
                "RIDER",
                "password",
                "93555557"
        );

        this.rider = new Rider(this.user2, "00-AA-11");
        this.customer = new Customer(
                this.user1,
                "Back street",
                "Very good restaurant, you can trust",
                "Restaurant",
                "PT50000201231234567890154"
        );

        this.comment = new Comment(this.rider, this.customer, 5, "perfect service");

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);

        this.customer.getComments().add(this.comment);
        this.entityManager.persist(this.customer);

        this.rider.getComments().add(this.comment);
        this.entityManager.persist(this.rider);

        this.entityManager.persist(this.comment);
        this.entityManager.flush();
    }

    @Test
    void whenUsersSubmitCommentsToARider_ThenTheCommentsShouldBeReturned() {
        var newComment = new Comment(this.rider, this.customer,
                4, "This time, the rider take a little bit more" +
                " to bring me pizza");

        var rider = this.riderRepository.getById(this.rider.getId());
        Assertions.assertThat(
                rider.getComments()
        ).contains(this.comment);

        // add new comment
        rider.getComments().add(newComment);

        this.entityManager.persist(rider);
        this.entityManager.persist(newComment);
        this.entityManager.flush();

        Assertions.assertThat(
                this.riderRepository.getById(this.rider.getId()).getComments()
        ).contains(this.comment, newComment);
    }

}