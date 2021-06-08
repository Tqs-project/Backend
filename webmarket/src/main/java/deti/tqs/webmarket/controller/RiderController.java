package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.service.RiderService;
import deti.tqs.webmarket.service.RiderServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

}
