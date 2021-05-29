package deti.tqs.webmarket.repository;

import deti.tqs.webmarket.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    private final User urlando = new User(
            "Urlando",
            "urlando@gmail.com",
            "ADMIN",
            "password",
            "93555555"
    );

    @Test
    void whenFindUrlandoByEmail_ThenReturnUrlando() {
        this.testEntityManager.persistAndFlush(urlando);

        User res = this.userRepository.findByEmail(urlando.getEmail());

        Assertions.assertThat(res).isEqualTo(
                urlando
        );
    }

    @Test
    void whenFindUrlandoByUsername_ThenReturnUrlando() {
        this.testEntityManager.persistAndFlush(urlando);

        User res = this.userRepository.findByUsername(urlando.getUsername());

        Assertions.assertThat(res).isEqualTo(
                urlando
        );
    }

    @Test
    void whenUnknownRoleIsPersisted_ThenReturnError() {
        var badUser = new User(
                "Im bad",
                "imbad@gmail.com",
                "NOT_ADMIN",
                "u-ll-never-find-this-one",
                "999999999"
        );

        Assertions.assertThatThrownBy(
                () -> this.testEntityManager.persistAndFlush(badUser)
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void testNotNullParams() {
        var noUsername = new User(
                null,
                "reallydontknow@gmail.com",
                "CUSTOMER",
                "pass",
                "111111111"
        );

        // verify null username exception
        Assertions.assertThatThrownBy(
                () -> this.testEntityManager.persistAndFlush(noUsername)
        ).isInstanceOf(PersistenceException.class);

        noUsername.setUsername("NowIHaveAName");
        noUsername.setEmail(null);

        // verify null email exception
        Assertions.assertThatThrownBy(
                () -> this.testEntityManager.persistAndFlush(noUsername)
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void whenAPhoneNumberWithSizeDiffFrom9IsPersisted_ThenReturnError() {
        var incorrectPhoneNumber = new User(
                "duno",
                "duno@gmail.com",
                "RIDER",
                "carochinha",
                "1234567890"
        );

        Assertions.assertThatThrownBy(
                () -> this.testEntityManager.persistAndFlush(incorrectPhoneNumber)
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void whenUniqueConstraintIsViolated_ThenReturnError() {
        var urlando2 = new User(
            urlando.getUsername(),
                urlando.getEmail(),
                urlando.getRole(),
                urlando.getPassword(),
                urlando.getPhoneNumber()
        );

        this.testEntityManager.persistAndFlush(urlando);

        Assertions.assertThatThrownBy(
                () -> this.testEntityManager.persistAndFlush(urlando2)
        ).isInstanceOf(PersistenceException.class);
    }

    @Test
    void testReturnOfAllTheRiders() {
        var user1 = new User(
                "user1",
                "user1@mail.com",
                "RIDER",
                "password",
                "111111111"
        );

        var user2 = new User(
                "user2",
                "user2@mail.com",
                "RIDER",
                "password",
                "111111111"
        );

        var user3 = new User(
                "user3",
                "user3@mail.com",
                "CUSTOMER",
                "password",
                "111111111"
        );

        this.testEntityManager.persist(user1);
        this.testEntityManager.persist(user2);
        this.testEntityManager.persist(user3);
        this.testEntityManager.flush();

        Assertions.assertThat(
                this.userRepository.findAllByRole("RIDER")
        ).contains(user1, user2).doesNotContain(user3);
    }
}