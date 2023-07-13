package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository repository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = getUser("name", "e@mail.com");
        itemRequest = getItemRequest("description", user);
    }

    @Nested
    class ReadTest {
        @Test
        void findAllByRequestorIdNotTest() {
            List<ItemRequest> found = repository.findAllByRequestorIdNot(user.getId() + 1, Pageable.ofSize(10));
            assertThat(found).isNotEmpty();
        }

        @Test
        void findAllByRequestorIdNot_OwnerTest() {
            List<ItemRequest> found = repository.findAllByRequestorIdNot(user.getId(), Pageable.ofSize(10));
            assertThat(found).isEmpty();
        }

        @Test
        void findAllByRequestorIdTest() {
            List<ItemRequest> found = repository.findAllByRequestorIdOrderByCreatedDesc(user.getId());
            assertThat(found).isNotEmpty();
            assertThat(found.size()).isEqualTo(1);
        }

        @Test
        void findAllByRequestorIdOrderByCreatedDescTest() {
            getItemRequest("desc2", user);
            List<ItemRequest> found = repository.findAllByRequestorIdOrderByCreatedDesc(user.getId());
            assertThat(found).isNotEmpty();
            assertThat(found.size()).isEqualTo(2);

            assertThat(found.get(0).getCreated()).isAfter(found.get(1).getCreated());
        }
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return entityManager.persist(user);
    }

    private ItemRequest getItemRequest(String description, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return entityManager.persist(itemRequest);
    }
}
