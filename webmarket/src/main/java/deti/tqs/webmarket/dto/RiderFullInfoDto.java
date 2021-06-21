package deti.tqs.webmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderFullInfoDto {
    private Long id;

    private String username;

    private String email;

    private String role;

    private String phoneNumber;

    private Date joinedAt;

    private String vehiclePlate;

    private List<Long> comments;

    private String lat;

    private String lng;

    private Boolean busy;

    private List<Long> rides;
}
