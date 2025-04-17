package edu.npic.smartBuilding.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "floors")
@AllArgsConstructor
@ToString
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private Integer roomQty;
    private String image;

//    relationship

    @ManyToOne
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Room> rooms;
}
