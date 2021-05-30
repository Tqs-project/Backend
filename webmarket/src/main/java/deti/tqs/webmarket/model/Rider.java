package deti.tqs.webmarket.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "riders")
public class Rider {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String vehiclePlate;

    @OneToMany(mappedBy = "rider")
    private List<Comment> comments;

    // location
    private String lat;

    private String lng;

    private Boolean busy;

    public Rider() {}

    public Rider(User user, String vehiclePlate) {
        this.user = user;
        this.vehiclePlate = vehiclePlate;
        this.comments = new ArrayList<>();
        this.busy = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rider rider = (Rider) o;
        return Objects.equals(id, rider.id) && Objects.equals(user, rider.user) && Objects.equals(vehiclePlate, rider.vehiclePlate) && Objects.equals(comments, rider.comments) && Objects.equals(lat, rider.lat) && Objects.equals(lng, rider.lng) && Objects.equals(busy, rider.busy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, vehiclePlate, comments, lat, lng, busy);
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

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Boolean getBusy() {
        return busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    @Override
    public String toString() {
        return "Rider{" +
                "id=" + id +
                ", user=" + user +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", comments=" + comments +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", busy=" + busy +
                '}';
    }
}
