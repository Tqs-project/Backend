package deti.tqs.webmarket.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Data
@Entity
@Table(name = "customer")
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

    @JsonIgnore
    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

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
}
