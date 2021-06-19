package deti.tqs.webmarket.controller;

import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import deti.tqs.webmarket.api.DistanceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FrontController {

    @Autowired
    private DistanceAPI distanceAPI;

    @GetMapping
    public String index() {
        return "Hello!! This is an API... so... sorry but in this site you won't see pretty CSS :(";
    }

    @GetMapping("test")
    public DistanceMatrix getRequest() {
        return distanceAPI.getDistance(new String[] { "Porto, Portugal"}, new String[] { "Lisboa, Portugal"});
    }
}
