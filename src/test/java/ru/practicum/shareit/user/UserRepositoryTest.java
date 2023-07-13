package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = getUser("name", "e@mail.com");
    }

    @Nested
    class CreateTests {
        @Test
        void createDoubleEmailTest() {
            User user = new User();
            user.setEmail("e@mail.com");
            assertThrows(DataIntegrityViolationException.class, () -> repository.save(user));
        }
    }

    @Nested
    class ReadTest {
        @Test
        void findByIdTest() {
            Optional<User> found = repository.findById(user.getId());
            assertThat(found).isNotEmpty();
            assertEquals(found.get(), user);
        }

        @Test
        void findByIdNotExistsTest() {
            Optional<User> found = repository.findById(-9L);
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class UpdateTest {
        @Test
        void saveTest() {
            user.setName("updName");
            User found = repository.save(user);

            assertThat(found.getName()).isEqualTo("updName");
        }
    }

    @Nested
    class DeleteTest {
        @Test
        void deleteTest() {
            Optional<User> found = repository.findById(user.getId());
            assertThat(found).isNotEmpty();

            repository.delete(user);

            found = repository.findById(user.getId());
            assertThat(found).isEmpty();
        }
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return entityManager.persist(user);
    }

}
