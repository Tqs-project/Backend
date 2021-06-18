package deti.tqs.webmarket.util;

import deti.tqs.webmarket.dto.*;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.Order;
import deti.tqs.webmarket.model.Rider;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Log4j2
public class Utils {
    private Utils() {}

    public static Timestamp parseTimestamp(String timestamp) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return new Timestamp(formatter.parse(timestamp).getTime());
        } catch (ParseException e) {
            log.error(String.format("Error parsing timestamp %s", timestamp));
            log.info("Returning timestamp with current time");
            return new Timestamp(System.currentTimeMillis());
        }
    }

    public static CustomerDto parseCustomerDto(Customer customer) {
        var comments = new ArrayList<Long>();
        var orders = new ArrayList<Long>();
        customer.getComments().forEach(
                comment -> comments.add(comment.getId())
        );
        customer.getOrders().forEach(
                order -> orders.add(order.getId())
        );
        return new CustomerDto(
                customer.getId(),
                customer.getUser().getUsername(),
                customer.getUser().getEmail(),
                customer.getUser().getRole(),
                "",
                customer.getUser().getPhoneNumber(),
                customer.getAddress(),
                customer.getDescription(),
                customer.getImageUrl(),
                customer.getTypeOfService(),
                customer.getIban(),
                comments,
                orders
        );
    }

    public static OrderDto parseOrderDto(Order order) {

        return new OrderDto(
                order.getId(),
                order.getOrderTimestamp(),
                order.getPaymentType(),
                order.getStatus(),
                order.getCost(),
                order.getCustomer().getAddress(),
                order.getLocation(),
                order.getCustomer() == null ? null : order.getCustomer().getId(),
                order.getCustomer() == null ? null : order.getCustomer().getUser().getUsername(),
                order.getRide() == null ? null : order.getRide().getId()
        );
    }

    public static RiderFullInfoDto parseRiderDto(Rider rider) {
        var comments = new ArrayList<Long>();
        rider.getComments().forEach(
                comment -> comments.add(comment.getId())
        );

        var rides = new ArrayList<Long>();
        rider.getRides().forEach(
                ride -> rides.add(ride.getId())
        );
        return new RiderFullInfoDto(
                rider.getId(),
                rider.getUser().getUsername(),
                rider.getUser().getEmail(),
                rider.getUser().getRole(),
                rider.getUser().getPhoneNumber(),
                rider.getVehiclePlate(),
                comments,
                rider.getLat(),
                rider.getLng(),
                rider.getBusy(),
                rides
            );
    }
}
