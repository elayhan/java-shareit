package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    private User user;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = getUser("name", "e@mail.com");
        user2 = getUser("name2", "a@mail.com");

        item = getItem("itemName", user, "description", true);

        booking = getBooking(user2,
                item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);
    }

    @Nested
    class FindByIdAndOwnerOrBookerTests {
        @Test
        void findByIdAndOwnerOrBookerTest_Owner() {
            Optional<Booking> found = repository.findByIdAndOwnerOrBooker(user.getId(), booking.getId());
            assertThat(found).isNotEmpty();
            validating(found.get(), booking);
        }

        @Test
        void findByIdAndOwnerOrBookerTest_Booker() {
            Optional<Booking> found = repository.findByIdAndOwnerOrBooker(user2.getId(), booking.getId());
            assertThat(found).isNotEmpty();
            validating(found.get(), booking);
        }

        @Test
        void findByIdAndOwnerOrBookerTest_NotExists() {
            Optional<Booking> found = repository.findByIdAndOwnerOrBooker(-3L, -1L);
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class FindLastBooking {
        @Test
        void findLastBookingTest() {
            Booking found = repository.findLastBooking(item.getId(), user.getId());
            validating(found, booking);
        }

        @Test
        void whenEmptyForUser2() {
            Booking notFound = repository.findLastBooking(item.getId(), user2.getId());
            assertThat(notFound).isNull();
        }
    }

    @Nested
    class FindNextBookingTests {
        @BeforeEach
        void setStartInFuture() {
            booking.setStart(LocalDateTime.now().plusHours(1));
            booking.setEnd(LocalDateTime.now().plusHours(2));

            entityManager.persist(booking);
        }

        @Test
        void findNextBookingTest() {
            Booking found = repository.findNextBooking(item.getId(), user.getId());
            assertThat(found).isNotNull();
            validating(found, booking);
        }

        @Test
        void whenEmptyForUser2() {
            Booking notFound = repository.findNextBooking(item.getId(), user2.getId());
            assertThat(notFound).isNull();
        }
    }

    @Nested
    class CheckBooked {
        @Test
        void checkBookedTest() {
            Optional<Booking> found = repository.checkBooked(item.getId(), user2.getId());
            assertThat(found).isNotEmpty();
            validating(found.get(), booking);
        }

        @Test
        void whenEmptyForUser() {
            Optional<Booking> notFound = repository.checkBooked(item.getId(), user.getId());
            assertThat(notFound).isEmpty();
        }
    }

    private void validating(Booking found, Booking except) {
        assertThat(found.getId()).isEqualTo(except.getId());
        assertThat(found.getStart()).isEqualTo(except.getStart());
        assertThat(found.getEnd()).isEqualTo(except.getEnd());
        assertThat(found.getItem()).isEqualTo(except.getItem());
        assertThat(found.getBooker()).isEqualTo(except.getBooker());
        assertThat(found.getStatus()).isEqualTo(except.getStatus());
        assertThat(found).isEqualTo(except);
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return entityManager.persist(user);
    }

    private Item getItem(String name, User owner, String description, Boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setOwner(owner);
        item.setDescription(description);
        item.setAvailable(available);

        return entityManager.persist(item);
    }

    private Booking getBooking(User user, Item item, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);

        return entityManager.persist(booking);
    }
}
