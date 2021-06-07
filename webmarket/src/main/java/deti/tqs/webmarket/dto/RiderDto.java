package deti.tqs.webmarket.dto;

import deti.tqs.webmarket.model.Comment;
import deti.tqs.webmarket.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderDto {

    private Long id;

    private String username;

    private String email;

    private String role;

    private String password;

    private String phoneNumber;

    private String vehiclePlate;

    private List<Comment> comments;

    private String lat;

    private String lng;

    private Boolean busy;

    private List<Ride> rides;

    private String iban;

    private List<Comment> comments;

    private List<Order> orders;

}