package deti.tqs.webmarket.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    @ManyToOne
    @JoinColumn(name = "commenter_id")
    private Customer commenter;

    private Integer stars;

    private String comment;

    private Timestamp timestamp;

    public Comment() {}

    public Comment(Rider rider, Customer customer, Integer stars, String comment) {
        this.rider = rider;
        this.commenter = customer;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

}
