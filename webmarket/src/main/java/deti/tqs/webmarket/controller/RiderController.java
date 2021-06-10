package deti.tqs.webmarket.controller;

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
@RequestMapping("/riders")
public class RiderController {
    @Autowired
    private RiderServiceImp service;

    @PostMapping("")
    public ResponseEntity<RiderDto> createRider(@RequestBody RiderDto riderDto) throws Exception {
        log.info("Saving rider " + riderDto.getUser().getUsername() + ".");
        return new ResponseEntity<>(service.registerRider(riderDto), HttpStatus.CREATED);
    }


    @GetMapping("")
    public List<Rider> getRiders(){
        return service.getAllRiders();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody RiderDto riderDto) {
        log.info("Logging in user");
        var response = this.service.login(riderDto);

        if (response.isEmpty())
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
