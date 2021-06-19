package deti.tqs.webmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimationDto {

    private String origin;

    private String destination;

    private String estimationTime;

    private Long distanceM;

    private String distanceKm;

    private Double deliveryPrice;
}
