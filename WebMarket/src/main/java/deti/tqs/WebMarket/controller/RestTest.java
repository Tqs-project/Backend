package deti.tqs.WebMarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class RestTest {
    
    @GetMapping("/test")
    public String test() {
        return "olá dps de github action";
    }
}
