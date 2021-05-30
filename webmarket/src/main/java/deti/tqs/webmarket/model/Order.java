package deti.tqs.webmarket.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp orderTimestamp;

    @Column(name = "payment_type",columnDefinition = "VARCHAR(20) CHECK (payment_type IN ('MB', 'PAYPAL', 'MBWAY'))")
    private String paymentType;

    @Column(columnDefinition = "VARCHAR(20) CHECK (status IN ('WAITING', 'DELIVERING', 'DELIVERED'))")
    private String status;

    private Float cost;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Ride ride;

    public Order() {}

    public Order(String paymentType, Float cost, Customer customer) {
        this.paymentType = paymentType;
        this.cost = cost;
        this.customer = customer;

        this.orderTimestamp = new Timestamp(System.currentTimeMillis());
        this.status = "WAITING";
    }
}
