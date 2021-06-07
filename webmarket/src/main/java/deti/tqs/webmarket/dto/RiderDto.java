package deti.tqs.webmarket.dto;

import deti.tqs.webmarket.model.Comment;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderDto {

    private UserDto user;

    private String vehiclePlate;

}