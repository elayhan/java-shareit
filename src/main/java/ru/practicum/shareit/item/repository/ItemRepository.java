package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByOwnerIdAndId(Long userId, Long id);

    Page<Item> findByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item as i where i.available = true " +
            "and (upper(name) like upper(concat('%', ?1, '%')) " +
            "or upper(description) like upper(concat('%', ?1, '%')) )")
    Page<Item> findByNameOrDescription(String match, Pageable pageable);

    Collection<Item> findAllByRequestId(Long request);

}
