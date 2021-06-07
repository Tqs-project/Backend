package deti.tqs.webmarket.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String address;

    private String description;

    private String imageUrl;

    private String typeOfService;

    @Column(length = 33)
    private String iban;

    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    // authentication purposes
    private String authToken;

    public Customer() {}

    public Customer(User user, String address, String description, String typeOfService, String iban) {
        this.user = user;
        this.address = address;
        this.description = description;
        this.typeOfService = typeOfService;
        this.iban = iban;

        this.comments = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + user +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", typeOfService='" + typeOfService + '\'' +
                ", iban='" + iban + '\'' +
                ", comments=" + comments.stream().map(
                comment -> comment.getId().toString()
        ).reduce("[", (partialString, identifier) -> partialString + ", " + identifier) + "]" +
                ", orders=" + orders.stream().map(
                comment -> comment.getId().toString()
        ).reduce("[", (partialString, identifier) -> partialString + ", " + identifier) + "]" +
                '}';
    }
}
