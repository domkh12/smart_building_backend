package edu.npic.smartBuilding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true, length = 200)
    private String email;
    @Column(nullable = false, length = 200)
    private String password;
    @Column(unique = true, length = 200)
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Boolean isOnline;
    private String status;
    private String address;

    // optional
    private String profileImage;
//    @Column(nullable = false)
    private Boolean isVerified;
    @Column(length = 200)
    private LocalDate dateOfBirth;
    private String BannerImage;

    // security
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Boolean isDeleted;
    private String twoFactorSecret;
    @Column(nullable = false)
    private Boolean isTwoFactorEnabled;

    // relationship

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private SignUpMethod signUpMethod;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id")
    )
    private List<Room> rooms;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
    private List<Device> devices;
}
