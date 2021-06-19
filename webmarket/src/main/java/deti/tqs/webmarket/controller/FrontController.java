package deti.tqs.webmarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FrontController {

    @GetMapping
    public String index() {
        return "Hello!! This is an API... so... sorry but in this site you won't see pretty CSS :(";
    }
}
