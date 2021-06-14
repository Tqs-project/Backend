package deti.tqs.webmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;

    private Timestamp orderTimestamp;

    private String paymentType;

    private String status;

    private Double cost;

    private String location;

    private Long customer_id;

    private String username;

    private Long ride_id;

}



