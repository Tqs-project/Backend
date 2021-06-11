package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.CustomerDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Customer;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.CustomerRepository;
import deti.tqs.webmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImpTest {

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private CustomerServiceImp customerService;

    private CustomerDto customerCreateDto;
    private CustomerDto customerCreateDtoRet;

    private User user;
    private Customer customer;
    private User userWithId;
    private Customer customerFromDb;

    private TokenDto token;

    @BeforeEach
    void setUp() {
        customerCreateDtoRet = new CustomerDto(
                3L,
                "Pedro",
                "pedro@gmail.com",
                "CUSTOMER",
                "",
                "935111111",
                "Front Street",
                "Wonderful coffee shop",
                null,
                "Coffee",
                "PT50000201231234567890155",
                new ArrayList<>(),
                new ArrayList<>()
        );
        customerCreateDto = new CustomerDto();
        customerCreateDto.setUsername("Pedro");
        customerCreateDto.setEmail("pedro@gmail.com");
        customerCreateDto.setPassword("password");
        customerCreateDto.setPhoneNumber("935111111");
        customerCreateDto.setAddress("Front Street");
        customerCreateDto.setDescription("Wonderful coffee shop");
        customerCreateDto.setTypeOfService("Coffee");
        customerCreateDto.setIban("PT50000201231234567890155");

        user = new User(
                customerCreateDto.getUsername(),
                customerCreateDto.getEmail(),
                "CUSTOMER",
                customerCreateDto.getPassword() + "-encoded",
                customerCreateDto.getPhoneNumber()
        );

        userWithId = new User(
                customerCreateDto.getUsername(),
                customerCreateDto.getEmail(),
                "CUSTOMER",
                customerCreateDto.getPassword() + "-encoded",
                customerCreateDto.getPhoneNumber()
        );
        userWithId.setId(3L);

        customer = new Customer(
                user,
                customerCreateDto.getAddress(),
                customerCreateDto.getDescription(),
                customerCreateDto.getTypeOfService(),
                customerCreateDto.getIban()
        );

        customerFromDb = new Customer(
                userWithId,
                customerCreateDto.getAddress(),
                customerCreateDto.getDescription(),
                customerCreateDto.getTypeOfService(),
                customerCreateDto.getIban()
        );
        customerFromDb.setId(userWithId.getId());

        userWithId.setCustomer(customerFromDb);

        token = new TokenDto("encrypted-token", "");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenCreateCustomerIsCalled_thenACustomerShouldBeAddedToTheDB() {
        Mockito.when(encoder.encode(customerCreateDto.getPassword())).thenReturn(
                customerCreateDto.getPassword() + "-encoded");

        Mockito.when(customerRepository.save(customer)).thenReturn(
                customerFromDb
        );

        assertThat(customerService.createCustomer(customerCreateDto)).isEqualTo(
                customerCreateDtoRet
        );
    }

    @Test
    void whenUpdatingCustomerAttributes_thenTheDatabaseShouldReflectDoesChanges() {
        customerCreateDto.setDescription("Now, even more wonderful");
        customerCreateDtoRet.setDescription(customerCreateDto.getDescription());
        customer.setDescription(customerCreateDto.getDescription());
        customerFromDb.setDescription(customerCreateDto.getDescription());

        customer.setUser(userWithId);
        customer.setId(userWithId.getId());

        Mockito.when(encoder.encode(customerCreateDto.getPassword())).thenReturn(
                customerCreateDto.getPassword() + "-encoded");

        Mockito.when(userRepository.findByUsername(customerCreateDto.getUsername()))
                .thenReturn(Optional.of(userWithId));

        Mockito.when(customerRepository.save(customer)).thenReturn(
                customerFromDb
        );

        assertThat(customerService.updateCustomer(customerCreateDto)).isEqualTo(
                customerCreateDtoRet
        );
    }

    @Test
    void whenUpdatingUnknownCustomer_ThenReturnException() {
        var unknownUser = "trambolho";
        Mockito.when(userRepository.findByUsername(unknownUser)).thenReturn(
                Optional.ofNullable(null)
        );

        customerCreateDto.setUsername(unknownUser);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(
                () -> customerService.updateCustomer(customerCreateDto)
        );

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(unknownUser);
    }

    @Test
    void whenLoginIsMadeWithUsername_thenReturnTokenToTheSession() {
        Mockito.when(encoder.encode(ArgumentMatchers.anyString())).thenReturn(
                "encrypted-token"
        );

        Mockito.when(encoder.matches(customerCreateDto.getPassword(),
                customerCreateDto.getPassword() + "-encoded"))
                .thenReturn(true);

        Mockito.when(userRepository.findByUsername(customerCreateDto.getUsername()))
                .thenReturn(Optional.of(userWithId));

        assertThat(customerService.login(customerCreateDto))
                .isEqualTo(token);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(
                customerCreateDto.getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(
                customerCreateDto.getEmail()
        );
    }

    @Test
    void whenLoginIsMadeWithEmail_thenReturnTokenToTheSession() {
        Mockito.when(encoder.encode(ArgumentMatchers.anyString())).thenReturn(
                "encrypted-token"
        );

        Mockito.when(encoder.matches(customerCreateDto.getPassword(),
                customerCreateDto.getPassword() + "-encoded"))
                .thenReturn(true);

        customerCreateDto.setUsername(null);
        Mockito.when(userRepository.findByEmail(customerCreateDto.getEmail()))
                .thenReturn(Optional.of(userWithId));

        assertThat(customerService.login(customerCreateDto))
                .isEqualTo(token);

        Mockito.verify(userRepository, Mockito.times(0)).findByUsername(
                customerCreateDto.getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(
                customerCreateDto.getEmail()
        );
    }

    @Test
    void whenLoginAndNoUsernameOrEmailIsProvided_thenReturnEmptyToken() {
        customerCreateDto.setUsername(null);
        customerCreateDto.setEmail(null);
        var tokenRet = new TokenDto(
                "", "Please provide username or email for authentication"
        );

        assertThat(customerService.login(customerCreateDto))
                .isEqualTo(tokenRet);

        Mockito.verify(userRepository, Mockito.times(0)).findByUsername(
                customerCreateDto.getUsername()
        );
        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(
                customerCreateDto.getEmail()
        );
    }

    @Test
    void whenLoginWithInvalidUsernameOrEmail_thenReturnEmptyToken() {
        var tokenRet = new TokenDto(
                "", "Bad authentication parameters"
        );

        Mockito.when(userRepository.findByUsername(customerCreateDto.getUsername()))
                .thenReturn(Optional.empty());

        assertThat(customerService.login(customerCreateDto))
                .isEqualTo(tokenRet);
    }

    @Test
    void whenLoginIsMadeWithInvalidPassword_thenReturnEmptyToken() {
        var invalidPassword = "im-the-exterminator";
        var tokenRet = new TokenDto(
                "", "Bad authentication parameters"
        );

        var correctPassword = customerCreateDto.getPassword();
        customerCreateDto.setPassword(invalidPassword);
        Mockito.when(encoder.matches(customerCreateDto.getPassword(),
                 correctPassword + "-encoded"))
                .thenReturn(false);

        customerCreateDto.setUsername(null);
        Mockito.when(userRepository.findByEmail(customerCreateDto.getEmail()))
                .thenReturn(Optional.of(userWithId));

        assertThat(customerService.login(customerCreateDto))
                .isEqualTo(tokenRet);

        Mockito.verify(encoder, Mockito.times(1)).matches(
                customerCreateDto.getPassword(), correctPassword + "-encoded"
        );
    }
}