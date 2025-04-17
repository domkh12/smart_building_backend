package edu.npic.smartBuilding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "device_types")
public class DeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean controllable;
    private String description;
    private String image;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "deviceType")
    private List<Device> devices;

}
