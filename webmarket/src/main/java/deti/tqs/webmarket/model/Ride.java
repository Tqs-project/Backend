package deti.tqs.webmarket.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
@Entity
@Table(name = "rides")
public class Ride {

    /**
    @Id
    @Column(name = "order_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;**/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String origin;

    private String destination;

    //private Double tripDistance;

    private String assignedRider;

    //private Timestamp timestampInit;

    //private Timestamp timestampEnd;

    public Ride() {}

    public Ride(String origin, String destination, String assignedRider) {
        this.origin = origin;
        this.destination = destination;
        this.assignedRider = assignedRider;
    }
}
