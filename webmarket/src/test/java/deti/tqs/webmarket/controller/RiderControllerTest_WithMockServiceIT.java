package deti.tqs.webmarket.controller;

import com.google.common.base.CharMatcher;
import deti.tqs.webmarket.dto.*;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.UserRepository;
import deti.tqs.webmarket.service.RiderService;
import deti.tqs.webmarket.service.RiderServiceImp;
import deti.tqs.webmarket.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
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

import java.sql.Timestamp;
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

    private RiderDto rider;
    private RiderDto riderResponse;

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

        rider = new RiderDto(
                new UserDto(
                        "Albert",
                        "albert@gmail.com",
                        "RIDER",
                        "password",
                        "935666122"
                ),
                "TH-32-8J");

        riderResponse = new RiderDto(
                new UserDto(
                        "Albert",
                        "albert@gmail.com",
                        "RIDER",
                        "",
                        "935666122"
                ),
                "TH-32-8J");
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

    @Test
    void whenPostRider_thenCreateRider() throws Exception{
        Mockito.when(riderService.registerRider(rider))
                .thenReturn(riderResponse);

        mvc.perform(
                post("/api/riders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(rider))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username", CoreMatchers.is(rider.getUser().getUsername())))
                .andExpect(jsonPath("$.user.password", CoreMatchers.is("")))
                .andExpect(jsonPath("$.user.role", CoreMatchers.is("RIDER")));

        Mockito.verify(riderService, Mockito.times(1)).registerRider(rider);
    }

    @Test
    void whenPostRiderLogin_ThenReturnToken() throws Exception {
        var token = new TokenDto("encrypted-token", "");

        var login = new CustomerLoginDto(
                rider.getUser().getUsername(),
                rider.getUser().getEmail(),
                rider.getUser().getPassword()
        );

        var riderForServiceClass = new RiderDto();
        var user = new UserDto();
        user.setUsername(login.getUsername());
        user.setEmail(login.getEmail());
        user.setPassword(login.getPassword());

        riderForServiceClass.setUser(user);

        Mockito.when(riderService.login(riderForServiceClass))
                .thenReturn(token);

        mvc.perform(
                post("/api/riders/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(login))
        ).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.token", CoreMatchers.is(token.getToken())));

        Mockito.verify(riderService, Mockito.times(1)).login(riderForServiceClass);
    }

    @Test
    void whenPostRiderLoginWithError_thenReturnEmptyToken() throws Exception {
        var token = new TokenDto("", "Some error occurred");

        var login = new CustomerLoginDto(
                rider.getUser().getUsername(),
                rider.getUser().getEmail(),
                rider.getUser().getPassword()
        );

        var riderForServiceClass = new RiderDto();
        var user = new UserDto();
        user.setUsername(login.getUsername());
        user.setEmail(login.getEmail());
        user.setPassword(login.getPassword());

        riderForServiceClass.setUser(user);

        Mockito.when(riderService.login(riderForServiceClass))
                .thenReturn(token);

        mvc.perform(
                post("/api/riders/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(login))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.token", CoreMatchers.is("")))
                .andExpect(jsonPath("$.errorMessage", CoreMatchers.is(token.getErrorMessage())));

        Mockito.verify(riderService, Mockito.times(1)).login(riderForServiceClass);
    }

    @Test
    void whenOnGetOrderAssignedTheRiderProvidesBadUsername_thenReturnEmptyOrderDto() throws Exception {
        var username = "i dont exist";
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        mvc.perform(
                get("/api/riders/order")
                .contentType(MediaType.APPLICATION_JSON)
                .header("username", username)
                .header("idToken", "verysecrettoken")
        ).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.id").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.status").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.cost").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.customerId").value(IsNull.nullValue()));
    }

    @Test
    void whenOnGetOrderAssignedTheRiderDoesntHaveLoginToken_thenReturnEmptyOrderDto() throws Exception {
        var username = "i dont exist";
        var token = "i dont exist also";
        var user = new User();
        user.setAuthToken("i do exist");
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        mvc.perform(
                get("/api/riders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", username)
                        .header("idToken", "verysecrettoken")
        ).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.id").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.status").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.cost").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.customerId").value(IsNull.nullValue()));
    }

    @Test
    void whenTheRiderDoesNotHaveAnyNewAssignment_thenReturnEmptyOrderDto() throws Exception {
        var username = "i dont exist";
        var token = "i do exist";
        var user = new User();
        user.setAuthToken(token);

        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        Mockito.when(riderService.riderHasNewAssignment(username))
                .thenReturn(false);

        mvc.perform(
                get("/api/riders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", username)
                        .header("idToken", token)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.status").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.cost").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.customerId").value(IsNull.nullValue()));

        Mockito.verify(riderService, Mockito.times(1))
                .riderHasNewAssignment(username);

        Mockito.verify(riderService, Mockito.times(0))
                .retrieveOrderAssigned(username);
    }

    @Test
    void whenEverythingIsOk_thenReturnOrderDto() throws Exception {
        var username = "i dont exist";
        var token = "i do exist";
        var user = new User();
        user.setAuthToken(token);

        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        Mockito.when(riderService.riderHasNewAssignment(username))
                .thenReturn(true);

        var orderDtoAssignOrder = new OrderDto(
                1L,
                new Timestamp(System.currentTimeMillis()),
                "PAYPAL",
                "WAITING",
                5.0,
                "heaven",
                12L,
                "Mercadona",
                null
        );

        Mockito.when(riderService.retrieveOrderAssigned(
                username
        )).thenReturn(orderDtoAssignOrder);

        mvc.perform(
                get("/api/riders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", username)
                        .header("idToken", token)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status", CoreMatchers.is(orderDtoAssignOrder.getStatus())))
                .andExpect(jsonPath("$.cost", CoreMatchers.is(orderDtoAssignOrder.getCost())))
                .andExpect(jsonPath("$.customerId").isNumber());

        Mockito.verify(riderService, Mockito.times(1))
                .riderHasNewAssignment(username);

        Mockito.verify(riderService, Mockito.times(1))
                .retrieveOrderAssigned(username);
    }

}