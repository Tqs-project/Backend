package deti.tqs.webmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private UserDto user;
    private String address;
    private String description;
    private String typeOfService;
    private String iban;
}
