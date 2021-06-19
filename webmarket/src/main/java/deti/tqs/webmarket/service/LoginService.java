package deti.tqs.webmarket.service;

import deti.tqs.webmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkLoginCredentials(String username, String token) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return false;

        if (!user.get().getRole().equals("ADMIN"))
            return false;

        if (!token.equals(user.get().getAuthToken()))
            return false;

        return true;
    }
}
