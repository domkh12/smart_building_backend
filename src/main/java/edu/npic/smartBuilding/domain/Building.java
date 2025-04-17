package edu.npic.smartBuilding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(nullable = false, unique = true)
    private String name;
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private Integer floorQty;
    private String image;


//    relationship
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Floor> floors;
}
