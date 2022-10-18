package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "items")
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Long id;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(nullable = false, length = 1000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(Long id, String name, String description, User owner, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }
}
