package deti.tqs.webmarket.util;

import deti.tqs.webmarket.dto.RiderFullInfoDto;
import deti.tqs.webmarket.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

class UtilsTest {

    @Test
    void parseTimestampTest() {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var timestamp = "2021-05-30 17:00:00";

        Timestamp testTimestamp;
        try {
            testTimestamp = new Timestamp(formatter.parse(timestamp).getTime());
        } catch (ParseException e) {
            testTimestamp = new Timestamp(System.currentTimeMillis());
        }

        Assertions.assertThat(
                Utils.parseTimestamp(timestamp)
        ).isEqualTo(testTimestamp);

        // check exception with point instead of minus
        Assertions.assertThat(
                Utils.parseTimestamp("2021.05-30 17:00:00")
        ).isNotEqualTo(testTimestamp);
    }

    @Test
    void parseCustomerDto() {
        var user = new User(
                "Jorge",
                "jorge@gmail.com",
                "RIDER",
                "password",
                "93555557"
        );

        var customer = new Customer(
                user,
                "Back street",
                "Very good restaurant, you can trust",
                "Restaurant",
                "PT50000201231234567890154"
        );

        var comment = new Comment();
        comment.setId(10L);
        var comment2 = new Comment();
        comment2.setId(11L);

        customer.setComments(Arrays.asList(comment, comment2));

        var parsing = Utils.parseCustomerDto(customer);
        Assertions.assertThat(
                parsing
        ).extracting("typeOfService").isEqualTo("Restaurant");

        Assertions.assertThat(
                parsing
        ).extracting("username").isEqualTo(user.getUsername());

        Assertions.assertThat(
                parsing
        ).extracting("comments").isEqualTo(Arrays.asList(10L, 11L));
    }

    @Test
    void parseOrderDto() {
        var order = new Order(
                "Raining Cash",
                30.0,
                null,
                "In a galaxy far away"
        );
        order.setId(1L);
        var customer = new Customer();
        customer.setAddress("Front Street hell");
        customer.setId(2L);

        var user = new User();
        user.setUsername(
                "Rogério"
        );
        customer.setUser(user);

        order.setCustomer(customer);

        var parsing = Utils.parseOrderDto(order);

        Assertions.assertThat(
                parsing
        ).extracting("id").isEqualTo(1L);

        Assertions.assertThat(
                parsing
        ).extracting("customerId").isEqualTo(2L);

        Assertions.assertThat(
                parsing
        ).extracting("username").isEqualTo(user.getUsername());
    }

    @Test
    void parseRiderDtoTest() {
        var rider = new Rider(
                new User(
                    "Pablo",
                        "soimepablo@gmail.com",
                        "RIDER",
                        "secret",
                        "935776666"
                ),
                "plate"
        );
        rider.setId(1L);
        rider.setLat("2");
        rider.setLng("3");
        rider.setBusy(false);

        var comment1 = new Comment();
        var comment2 = new Comment();
        comment1.setId(100L);
        comment2.setId(111L);

        rider.setComments(
                Arrays.asList(
                        comment1,
                        comment2
                )
        );
        rider.setRides(new ArrayList<>());

        var parsed = Utils.parseRiderDto(rider);

        Assertions.assertThat(
                parsed
        ).extracting(RiderFullInfoDto::getUsername)
                .isEqualTo(rider.getUser().getUsername());

        Assertions.assertThat(
                parsed
        ).extracting(RiderFullInfoDto::getComments)
                .isEqualTo(
                        Arrays.asList(100L, 111L)
                );
    }
}