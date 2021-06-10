package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.repository.RideRepository;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.RiderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/rider")
public class RiderController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/ride/{id}/delivered")
    public ResponseEntity<String> updateOrderDelivered(@PathVariable Long rideId,
                                                       @RequestHeader String idToken,
                                                       @RequestHeader String username) {
        /**
         * updating the order to DELIVERED state
         * if the user is authenticated
         */

        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return new ResponseEntity<>("Invalid username", HttpStatus.UNAUTHORIZED);

        if (!idToken.equals(user.get().getAuthToken()))
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);

        if (!riderService.updateOrderDelivered(rideId))
            return new ResponseEntity<>("No ride with id: " + rideId, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("Ride updated with success", HttpStatus.OK);
    }

}
