package deti.tqs.webmarket.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TokenDtoTest {

    private TokenDto token1;
    private TokenDto token2;

    @BeforeEach
    void setUp() {
        token1 = new TokenDto("Not empty token", "");
        token2 = new TokenDto("", "Empty token");
    }

    @Test
    void isEmpty() {
        assertThat(token1.isEmpty()).isFalse();
        assertThat(token2.isEmpty()).isTrue();
    }
}