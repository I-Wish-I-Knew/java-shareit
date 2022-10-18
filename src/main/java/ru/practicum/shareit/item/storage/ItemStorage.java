package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where i.id = :id " +
            "and i.owner.id <> :ownerId")
    Optional<Item> findByIdWhereOwnerIdNot(@Param("id") Long id,
                                           @Param("ownerId") Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);

    void deleteItemByIdAndOwnerId(Long id, Long ownerId);

    @Query(value = "select * from items i " +
            "where (i.item_name ilike %?1% " +
            "or i.description ilike %?1% ) " +
            "and i.available = true " +
            "order by i.item_id", nativeQuery = true)
    List<Item> findAllByNameOrDescriptionLike(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);
}
