package deti.tqs.webmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDto {

    private String username;

    private String email;

    private String password;

    private String phoneNumber;

    private String address;

    private String description;

    private String imageUrl;

    private String typeOfService;

    private String iban;
}
