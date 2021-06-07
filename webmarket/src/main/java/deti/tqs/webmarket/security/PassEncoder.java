package deti.tqs.webmarket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PassEncoder {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder encoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
