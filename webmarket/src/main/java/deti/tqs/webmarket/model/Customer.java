package deti.tqs.webmarket.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String IBAN;

    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;

    public Customer() {}

    public Customer(User user, String address, String description, String typeOfService, String IBAN) {
        this.user = user;
        this.address = address;
        this.description = description;
        this.typeOfService = typeOfService;
        this.IBAN = IBAN;
        this.comments = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(user, customer.user) && Objects.equals(address, customer.address) && Objects.equals(description, customer.description) && Objects.equals(imageUrl, customer.imageUrl) && Objects.equals(typeOfService, customer.typeOfService) && Objects.equals(IBAN, customer.IBAN) && Objects.equals(comments, customer.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, address, description, imageUrl, typeOfService, IBAN, comments);
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
                ", IBAN='" + IBAN + '\'' +
                ", comments=" + comments +
                '}';
    }
}
