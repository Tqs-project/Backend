package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.AdminService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Queue;

@Log4j2
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;

    @GetMapping("/orderscache/assignments")
    public ResponseEntity<Map<String, Long>> getCurrentAssignments() {
        // TODO authentication
        return new ResponseEntity<>(
                this.adminService.getCurrentAssignments(),
                HttpStatus.OK
        );
    }

    @GetMapping("/orderscache/waitingqueue")
    public ResponseEntity<Queue<Long>> getWaitingQueueOrders() {
        return new ResponseEntity<>(
                this.adminService.getWaitingOrdersAssignment(),
                HttpStatus.OK
        );
    }

    @PostMapping("/orderscache/reset")
    public void resetOrdersCache() {
        this.adminService.resetOrdersCache();
    }
}
