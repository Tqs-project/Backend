package deti.tqs.webmarket.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(mappedBy = "rider")
    private List<Ride> rides;

    public Rider() {}

    public Rider(User user, String vehiclePlate) {
        this.user = user;
        this.vehiclePlate = vehiclePlate;
        this.comments = new ArrayList<>();
        this.rides = new ArrayList<>();
        this.busy = false;
    }

    @Override
    public String toString() {
        return "Rider{" +
                "id=" + id +
                ", user=" + user +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", comments=" + comments.stream().map(
                comment -> comment.getId().toString()
        ).reduce("[", (partialString, identifier) -> partialString + ", " + identifier) + "]" +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", busy=" + busy +
                ", rides=" + rides.stream().map(
                ride -> ride.getId().toString()
        ).reduce("[", (partialString, identifier) -> partialString + ", " + identifier) + "]" +
                '}';
    }
}
