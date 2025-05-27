package edu.npic.smartBuilding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "rooms")
@ToString
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String uuid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer devicesQty;

    //optional
    private String image;
    private LocalDateTime createdAt;

    // relationship
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Device> devices;

    @ManyToOne
    private Floor floor;

    @ManyToMany(mappedBy = "rooms")
    private List<User> users;
}
