package deti.tqs.webmarket.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @Column(name = "order_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    private String origin;

    private String destination;

    //private Double tripDistance;

    private Timestamp timestampInit;

    private Timestamp timestampEnd;

    @ManyToOne
    @JoinColumn(name = "assigned_rider")
    private Rider rider;

    public Ride() {}

    public Ride(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;

        this.timestampInit = new Timestamp(System.currentTimeMillis());
    }
}
