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

import java.text.SimpleDateFormat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User user1;
    private User user2;
    private Rider rider;
    private Customer customer;
    private Comment comment1;
    private Comment comment2;

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

        this.comment1 = new Comment(this.rider, this.customer, 5, "perfect service");
        this.comment2 = new Comment(this.rider, this.customer, 4, "i saw a hair in the middle of my pizza, it was your");
        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);

        this.customer.getComments().add(this.comment1);
        this.customer.getComments().add(this.comment2);
        this.entityManager.persist(this.customer);

        this.rider.getComments().add(this.comment1);
        this.rider.getComments().add(this.comment2);
        this.entityManager.persist(this.rider);

        this.entityManager.persist(this.comment1);
        this.entityManager.persist(this.comment2);
        this.entityManager.flush();
    }

    @Test
    void returnAllRiderCommentsTest() {
        Assertions.assertThat(
                this.commentRepository.findCommentsByRider(this.rider)
        ).contains(this.comment1, this.comment2);
    }

    @Test
    void returnCommentsMadeByCustomerTest() {
        Assertions.assertThat(
                this.commentRepository.findCommentsByCommenter(this.customer)
        ).contains(this.comment1, this.comment2);
    }

    @Test
    void whenCommentDoesntBelongToCustomer_thenThatCommentShouldntBeReturned() {
        var user3 = new User(
                "Andrade",
                "andrade@gmail.com",
                "CUSTOMER",
                "password",
                "93555558"
        );

        var customer2 = new Customer(
                user3,
                "Front street",
                "Not so good restaurant, but you will eat..." +
                        "our marketing guys are really good",
                "Restaurant",
                "PT50000201231234567890152"
        );

        var newComment = new Comment(
                this.rider,
                customer2,
                3,
                "Disgusted. The pizza arrived half eaten."
        );

        customer2.getComments().add(newComment);
        this.rider.getComments().add(newComment);

        this.entityManager.persist(customer2);
        this.entityManager.persist(this.rider);
        this.entityManager.persist(newComment);
        this.entityManager.flush();

        // verify if comment was added to rider
        Assertions.assertThat(
                this.commentRepository.findCommentsByRider(this.rider)
        ).contains(this.comment2, this.comment1, newComment);

        // verify if the comment wasn't added to customer1
        Assertions.assertThat(
                this.commentRepository.findCommentsByCommenter(this.customer)
        ).contains(this.comment1, this.comment2).doesNotContain(newComment);

        // verify the comment on customer2
        Assertions.assertThat(
                this.commentRepository.findCommentsByCommenter(customer2)
        ).contains(newComment);
    }

    @Test
    void timestampAfterTest_returnAllCommentsAfterACertainTimestamp() {
        var comments = this.commentRepository.findAll();

        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // set TimeStamp
        comments.forEach(
                comment -> comment.setTimestamp(
                        
                )
        );
    }
}