package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.service.RiderService;
import deti.tqs.webmarket.service.RiderServiceImp;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/riders")
public class RiderController {
    @Autowired
    private RiderServiceImp service;

    @PostMapping("")
    public Rider createRider(@Valid @RequestBody RiderDto riderDto) throws Exception {
        return service.registerRider(riderDto);
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
