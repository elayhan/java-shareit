package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;


    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("e@mail.com");

        user = entityManager.persist(user);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("a@mail.com");

        user2 = entityManager.persist(user2);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("requestItem");
        itemRequest.setRequestor(user2);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = entityManager.persist(itemRequest);

        item = new Item();
        item.setOwner(user);
        item.setName("item");
        item.setAvailable(true);
        item.setDescription("description");
        item.setRequest(itemRequest);

        item = entityManager.persist(item);
    }

    @Nested
    class FindByNameOrDescriptionTests {

        @Test
        void findByNameOrDescriptionTest() {
            List<Item> found = repository.findByNameOrDescription("TE", Pageable.ofSize(10));
            assertThat(found).isNotEmpty();
            verify(found.get(0), item);
        }

        @Test
        void findByNameOrDescriptionNotFoundTest() {
            List<Item> found = repository.findByNameOrDescription("notF", Pageable.ofSize(10));
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class FindByOwnerIdAndIdTests {
        @Test
        void findByOwnerIdAndIdTest() {
            Optional<Item> found = repository.findByOwnerIdAndId(user.getId(), item.getId());
            assertThat(found).isNotEmpty();
            assertThat(found.get()).isEqualTo(item);
        }

        @Test
        void findByOwnerIdAndIdNotFoundTest() {
            Optional<Item> found = repository.findByOwnerIdAndId(user2.getId(), item.getId());
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class FindByOwnerIdTests {
        @Test
        void findByOwnerIdTest() {
            List<Item> found = repository.findByOwnerId(user.getId(), Pageable.ofSize(10));
            assertThat(found).isNotEmpty();
            verify(found.get(0), item);
        }

        @Test
        void findByOwnerIdNotFoundTest() {
            List<Item> found = repository.findByOwnerId(user2.getId(), Pageable.ofSize(10));
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class FindAllByRequestId {
        Item item2 = new Item();

        @BeforeEach
        void addItem() {
            item2.setRequest(itemRequest);
            item2.setOwner(user);
            item2.setName("item2");
            entityManager.persist(item2);
        }

        @Test
        void findAllByRequestId() {
            List<Item> found = repository.findAllByRequestId(itemRequest.getId());
            assertThat(found).isNotEmpty();
            verify(found.get(0), item);
            verify(found.get(1), item2);
        }
    }

    private void verify(Item found, Item expected) {
        assertThat(found).isEqualTo(expected);
        assertThat(found.getName()).isEqualTo(expected.getName());
        assertThat(found.getDescription()).isEqualTo(expected.getDescription());
        assertThat(found.getOwner().getEmail()).isEqualTo(expected.getOwner().getEmail());
        assertThat(found.getRequest().getCreated()).isNotNull();
    }
}
