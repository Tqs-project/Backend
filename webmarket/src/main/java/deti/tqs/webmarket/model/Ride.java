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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    private String destination;

    //private Double tripDistance;

    // starts when the rider accepts the order
    private Timestamp timestampInit;

    // ends when the order is updated to DELIVERED STATE
    private Timestamp timestampEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_rider")
    private Rider rider;

    public Ride() {}

    public Ride(Order order, String destination) {
        this.order = order;
        this.destination = destination;
    }
}
