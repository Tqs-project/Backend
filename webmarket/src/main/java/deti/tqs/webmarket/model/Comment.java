package deti.tqs.webmarket.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public Customer getCommenter() {
        return commenter;
    }

    public void setCommenter(Customer commenter) {
        this.commenter = commenter;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return Objects.equals(id, comment1.id) && Objects.equals(rider, comment1.rider) && Objects.equals(commenter, comment1.commenter) && Objects.equals(stars, comment1.stars) && Objects.equals(comment, comment1.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rider, commenter, stars, comment);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", rider=" + rider +
                ", customer=" + commenter +
                ", stars=" + stars +
                ", comment='" + comment + '\'' +
                '}';
    }
}
