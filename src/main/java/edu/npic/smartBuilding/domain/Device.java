package edu.npic.smartBuilding.domain;

import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.base.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String uuid;
    @Column(nullable = false, length = 50)
    private String name;
    private LocalDateTime createdAt;
    private String image;
    private DeviceStatus status;

    @ManyToOne
    private Room room;

    @ManyToMany
    private List<User> users;

    @ManyToOne
    private DeviceType deviceType;

    @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
    private List<Event> events;
}
