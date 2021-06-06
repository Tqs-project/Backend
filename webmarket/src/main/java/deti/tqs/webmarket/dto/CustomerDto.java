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
public class CustomerDto {

    private Long id;

    private String username;

    private String email;

    private String role;

    private String password;

    private String phoneNumber;

    private String address;

    private String description;

    private String imageUrl;

    private String typeOfService;

    private String iban;

    private List<Comment> comments;

    private List<Order> orders;

}
