package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.service.RiderServiceImp;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/riders")
public class RiderController {
    @Autowired
    private RiderServiceImp riderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<RiderDto> createRider(@RequestBody RiderDto riderDto) throws Exception {
        log.info("Saving rider " + riderDto.getUser().getUsername() + ".");
        return new ResponseEntity<>(riderService.registerRider(riderDto), HttpStatus.CREATED);
    }


    @GetMapping("")
    public List<Rider> getRiders(){
        return riderService.getAllRiders();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody RiderDto riderDto) {
        log.info("Logging in user");
        var response = this.riderService.login(riderDto);

        if (response.isEmpty())
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/ride/{id}/delivered")
    public ResponseEntity<String> updateOrderDelivered(@PathVariable Long id,
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

        if (!riderService.updateOrderDelivered(id))
            return new ResponseEntity<>("No ride with id: " + id, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("Ride updated with success", HttpStatus.OK);
    }

}
