package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.OrderDto;
import deti.tqs.webmarket.dto.RiderFullInfoDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.AdminService;
import deti.tqs.webmarket.service.LoginService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Queue;

@Log4j2
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getCustomers(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (!loginService.checkLoginCredentials(username, idToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.adminService.getCustomers(),
                HttpStatus.OK
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (!loginService.checkLoginCredentials(username, idToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.adminService.getOrders(),
                HttpStatus.OK
        );
    }

    @GetMapping("/riders")
    public ResponseEntity<List<RiderFullInfoDto>> getRiders(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (!loginService.checkLoginCredentials(username, idToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.adminService.getRiders(),
                HttpStatus.OK
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(

    ) {

    }

    // TODO login e logout

    @GetMapping("/orderscache/assignments")
    public ResponseEntity<Map<String, Long>> getCurrentAssignments(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (!loginService.checkLoginCredentials(username, idToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.adminService.getCurrentAssignments(),
                HttpStatus.OK
        );
    }

    @GetMapping("/orderscache/waitingqueue")
    public ResponseEntity<Queue<Long>> getWaitingQueueOrders(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (!loginService.checkLoginCredentials(username, idToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(
                this.adminService.getWaitingOrdersAssignment(),
                HttpStatus.OK
        );
    }

    @PostMapping("/orderscache/reset")
    public void resetOrdersCache(
            @RequestHeader String username,
            @RequestHeader String idToken
    ) {
        if (loginService.checkLoginCredentials(username, idToken))
            this.adminService.resetOrdersCache();
    }
}
