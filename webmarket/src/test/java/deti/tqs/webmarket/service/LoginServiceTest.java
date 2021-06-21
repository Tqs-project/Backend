package deti.tqs.webmarket.service;

import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    private String username;
    private String token;
    private User user;

    @BeforeEach
    void setup() {
        username = "Candido";
        token = "ssshhhshhhs";

        user = new User();
        user.setRole("ADMIN");
        user.setAuthToken(token);
    }

    @Test
    void checkGoodLoginCredentialsTest() {
        Mockito.when(userRepository.findByUsername(
                username
        )).thenReturn(Optional.of(user));

        Assertions.assertThat(
                loginService.checkLoginCredentials(
                        username, token
                )
        ).isTrue();
    }

    @Test
    void checkNoUserFoundTest() {
        Mockito.when(userRepository.findByUsername(
                username
        )).thenReturn(Optional.empty());

        Assertions.assertThat(
                loginService.checkLoginCredentials(
                        username, token
                )
        ).isFalse();
    }

    @Test
    void checkUserIsNotAdminTest() {
        user.setRole("not admin");
        Mockito.when(userRepository.findByUsername(
                username
        )).thenReturn(Optional.of(user));

        Assertions.assertThat(
                loginService.checkLoginCredentials(
                        username, token
                )
        ).isFalse();
    }

    @Test
    void checkUserIncorrectTokenTest() {
        user.setAuthToken("a very bad token");
        Mockito.when(userRepository.findByUsername(
                username
        )).thenReturn(Optional.empty());

        Assertions.assertThat(
                loginService.checkLoginCredentials(
                        username, token
                )
        ).isFalse();
    }
}