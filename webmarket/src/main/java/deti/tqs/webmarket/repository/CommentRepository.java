package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.Comment;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByRider(Rider rider);
    List<Comment> findCommentsByCommenter(Customer customer);
    List<Comment> findCommentsByTimestampAfter(Timestamp timestamp);
    List<Comment> findCommentsByTimestampBefore(Timestamp timestamp);
}
