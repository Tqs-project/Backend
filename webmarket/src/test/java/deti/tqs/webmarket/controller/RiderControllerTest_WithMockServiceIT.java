package deti.tqs.webmarket.controller;

import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.RiderService;
import deti.tqs.webmarket.service.RiderServiceImp;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Log4j2
@WebMvcTest(RiderController.class)
class RiderControllerTest_WithMockServiceIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RiderServiceImp riderService;

    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach()
    void setup() {
        user = new User(
                "Albert",
                "albert@gmail.com",
                "RIDER",
                "password",
                "935666122"
        );

        user.setAuthToken("token-super-secret");
    }

    @AfterEach()
    void tearDown() {}

    @Test
    void whenNoTokenIsProvided_thenReturnMissingRequestHeaderException() throws Exception{
        /**
         * when no token is provided
         * a error response should be retrieved from server
         * error -> MissingRequestHeaderException
         *
         * the implies to the username header
         */

        mvc.perform(
                post("/api/riders/ride/1/delivered")
                .contentType(MediaType.APPLICATION_JSON)
                .header("username", "Foo")
        ).andExpect(status().isBadRequest())
            .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException))
            .andExpect(result -> Assertions.assertEquals(
                    "Required request header 'idToken' for method parameter type String is not present",
                    result.getResolvedException().getMessage()));
    }

    @Test
    void whenUsernameProvidedDoesNotExist_thenReturnUnauthorizedResponse() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(
                Optional.empty()
        );

        mvc.perform(
                post("/api/riders/ride/1/delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                .header("username", user.getUsername())
                .header("idToken", "tokensupersecret")
        ).andExpect(status().isUnauthorized())
            .andExpect(result -> Assertions.assertEquals(
                    "Invalid username",
                    result.getResponse().getContentAsString()
            ));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(riderService, Mockito.times(0)).updateOrderDelivered(1L);
    }

    @Test
    void whenTokenIsNotCorrect_thenReturnUnauthorizedResponse() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(
                Optional.of(user)
        );

        mvc.perform(
                post("/api/riders/ride/1/delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", user.getUsername())
                        .header("idToken", "tokensupersecret")
        ).andExpect(status().isUnauthorized())
                .andExpect(result -> Assertions.assertEquals(
                        "Invalid token",
                        result.getResponse().getContentAsString()
                ));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(riderService, Mockito.times(0)).updateOrderDelivered(1L);
    }

    @Test
    void whenRideIdIsInvalid_thenReturnNotFoundResponse() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(
                Optional.of(user)
        );

        Mockito.when(riderService.updateOrderDelivered(12L)).thenReturn(
                false
        );

        mvc.perform(
                post("/api/riders/ride/12/delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", user.getUsername())
                        .header("idToken", user.getAuthToken())
        ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertEquals(
                        "No ride with id: 12",
                        result.getResponse().getContentAsString()
                ));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(riderService, Mockito.times(1)).updateOrderDelivered(12L);
    }

    @Test
    void whenEverythingIsOk_thenReturnOkResponse() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(
                Optional.of(user)
        );

        Mockito.when(riderService.updateOrderDelivered(1L)).thenReturn(
                true
        );

        mvc.perform(
                post("/api/riders/ride/1/delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", user.getUsername())
                        .header("idToken", user.getAuthToken())
        ).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(
                        "Ride updated with success",
                        result.getResponse().getContentAsString()
                ));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(riderService, Mockito.times(1)).updateOrderDelivered(1L);
    }
}