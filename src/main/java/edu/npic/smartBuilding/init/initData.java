package edu.npic.smartBuilding.init;

import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.base.Status;
import edu.npic.smartBuilding.domain.*;
import edu.npic.smartBuilding.features.building.BuildingRepository;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.deviceType.DeviceTypeRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.floor.FloorRepository;
import edu.npic.smartBuilding.features.gender.GenderRepository;
import edu.npic.smartBuilding.features.role.RoleRepository;
import edu.npic.smartBuilding.features.room.RoomRepository;
import edu.npic.smartBuilding.features.signUpMethod.SignUpMethodRepository;
import edu.npic.smartBuilding.features.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class initData {
    private final DeviceRepository deviceRepository;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SignUpMethodRepository signUpMethodRepository;
    private final GenderRepository genderRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;

    @PostConstruct
    public void init() {
        try {
            initBuildingData();
            initFloorData();
            initRoomData();
            initDeviceTypeData();
            initDeviceData();
            initGenderData();
            initRoles();
            initSignUpMethodData();
            initUsersData();
        } catch (Exception e) {
            System.err.println("Error during initializations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initBuildingData(){
        Building building = new Building();
        building.setUuid(UUID.randomUUID().toString());
        building.setName("Building Name");
        building.setFloorQty(0);
        building.setCreatedAt(LocalDateTime.now());
        buildingRepository.save(building);
    }

    private void initFloorData(){
        Floor floor = new Floor();
        Building building = buildingRepository.findById(1).orElseThrow();
        floor.setUuid(UUID.randomUUID().toString());
        floor.setName("Floor Name");
        floor.setBuilding(building);
        floor.setCreatedAt(LocalDateTime.now());
        floor.setRoomQty(0);
        building.setFloorQty(building.getFloorQty() + 1);
        buildingRepository.save(building);
        floorRepository.save(floor);
    }

    private void initRoomData(){
        Floor floor = floorRepository.findById(1).orElseThrow();
        Room room = new Room();
        room.setUuid(UUID.randomUUID().toString());
        room.setName("Room Name");
        room.setCreatedAt(LocalDateTime.now());
        room.setFloor(floor);
        room.setDevicesQty(0);
        floor.setRoomQty(floor.getRoomQty() + 1);
        floorRepository.save(floor);
        roomRepository.save(room);
    }

    private void initDeviceTypeData(){
        DeviceType deviceType = new DeviceType();
        deviceType.setUuid(UUID.randomUUID().toString());
        deviceType.setName("Temperature");
        deviceType.setControllable(false);
        deviceType.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType);

        DeviceType deviceType1 = new DeviceType();
        deviceType1.setUuid(UUID.randomUUID().toString());
        deviceType1.setName("Humidity");
        deviceType1.setControllable(false);
        deviceType1.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType1);

        DeviceType deviceType2 = new DeviceType();
        deviceType2.setUuid(UUID.randomUUID().toString());
        deviceType2.setName("Light");
        deviceType2.setControllable(true);
        deviceType2.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType2);


        DeviceType deviceType3 = new DeviceType();
        deviceType3.setUuid(UUID.randomUUID().toString());
        deviceType3.setName("Power");
        deviceType3.setControllable(false);
        deviceType3.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType3);

        DeviceType deviceType4 = new DeviceType();
        deviceType4.setUuid(UUID.randomUUID().toString());
        deviceType4.setName("PM2_5");
        deviceType4.setControllable(false);
        deviceType4.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType4);
    }

    private void initDeviceData(){

        DeviceType temperatureType = deviceTypeRepository.findById(1).orElseThrow();
        DeviceType humidityType = deviceTypeRepository.findById(2).orElseThrow();
        DeviceType lampType = deviceTypeRepository.findById(3).orElseThrow();
        DeviceType powerType = deviceTypeRepository.findById(4).orElseThrow();
        DeviceType pm2_5Type = deviceTypeRepository.findById(5).orElseThrow();

        Room room = roomRepository.findById(1).orElseThrow();
        Device device = new Device();
        device.setUuid(UUID.randomUUID().toString());
        device.setName("Temperature");
        device.setDeviceType(temperatureType);
        device.setCreatedAt(LocalDateTime.now());
        device.setStatus(DeviceStatus.Inactive);
        device.setEvents(new ArrayList<>());
        device.setRoom(room);
        room.setDevicesQty(room.getDevicesQty() + 1);
        roomRepository.save(room);
        deviceRepository.save(device);

        Device device1 = new Device();
        device1.setUuid(UUID.randomUUID().toString());
        device1.setName("Humidity");
        device1.setDeviceType(humidityType);
        device1.setCreatedAt(LocalDateTime.now());
        device1.setStatus(DeviceStatus.Inactive);
        device1.setEvents(new ArrayList<>());
        device1.setRoom(room);
        room.setDevicesQty(room.getDevicesQty() + 1);
        roomRepository.save(room);
        deviceRepository.save(device1);

        for (int i = 1; i < 5; i++) {


            Device device2 = new Device();
            device2.setUuid(UUID.randomUUID().toString());
            device2.setName("Lamp" + i);
            device2.setDeviceType(lampType);
            device2.setCreatedAt(LocalDateTime.now());
            device2.setStatus(DeviceStatus.Inactive);
            device2.setRoom(room);
            room.setDevicesQty(room.getDevicesQty() + 1);


            roomRepository.save(room);
            Device savedDevice = deviceRepository.save(device2);
            Event event = new Event();
            event.setUuid(UUID.randomUUID().toString());
            event.setValue("0");
            event.setCreatedAt(LocalDateTime.now());
            event.setDevice(savedDevice);
            eventRepository.save(event);
        }

        Device device3 = new Device();
        device3.setUuid(UUID.randomUUID().toString());
        device3.setName("Power");
        device3.setDeviceType(powerType);
        device3.setCreatedAt(LocalDateTime.now());
        device3.setStatus(DeviceStatus.Inactive);
        device3.setEvents(new ArrayList<>());
        device3.setRoom(room);
        room.setDevicesQty(room.getDevicesQty() + 1);
        roomRepository.save(room);
        deviceRepository.save(device3);

        Device device4 = new Device();
        device4.setUuid(UUID.randomUUID().toString());
        device4.setName("PM2_5");
        device4.setDeviceType(pm2_5Type);
        device4.setCreatedAt(LocalDateTime.now());
        device4.setStatus(DeviceStatus.Inactive);
        device4.setEvents(new ArrayList<>());
        device4.setRoom(room);
        room.setDevicesQty(room.getDevicesQty() + 1);
        roomRepository.save(room);
        deviceRepository.save(device4);


    }

    private void initGenderData(){
        Gender male = new Gender();
        male.setUuid(UUID.randomUUID().toString());
        male.setGender("Male");
        Gender female = new Gender();
        female.setUuid(UUID.randomUUID().toString());
        female.setGender("Female");
        genderRepository.saveAll(List.of(male, female));
    }

    private void initSignUpMethodData(){
        SignUpMethod signUpMethod = new SignUpMethod();
        signUpMethod.setUuid(UUID.randomUUID().toString());
        signUpMethod.setName("AZURE");
        SignUpMethod signUpMethod2 = new SignUpMethod();
        signUpMethod2.setUuid(UUID.randomUUID().toString());
        signUpMethod2.setName("CUSTOM");
        signUpMethodRepository.save(signUpMethod2);
        signUpMethodRepository.save(signUpMethod);
    }

    private void initUsersData() {
        User user = new User();

        user.setFullName("NPIC");
        user.setPassword(passwordEncoder.encode("Npic@2024"));

        // Assign specific email for the first user
        user.setEmail("npic@gmail.com");

        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setGender(genderRepository.findById(1).get());
        user.setPhoneNumber("0877345470");
        user.setUuid(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsVerified(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsDeleted(false);
        user.setIsOnline(false);
        user.setSignUpMethod(signUpMethodRepository.findByName("CUSTOM").get());
        user.setIsTwoFactorEnabled(false);
        user.setStatus(String.valueOf(Status.Active));

        List<Role> roles = new ArrayList<>();
        // Ensure roles exist
        roles.add(roleRepository.findById(1).orElseThrow(() -> new RuntimeException("Role 1 not found")));

        user.setRoles(roles);

        userRepository.save(user);
    }

    private void initRoles(){
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().uuid(UUID.randomUUID().toString()).name("MANAGER").build());
        roles.add(Role.builder().uuid(UUID.randomUUID().toString()).name("ADMIN").build());
        roles.add(Role.builder().uuid(UUID.randomUUID().toString()).name("USER").build());

        roleRepository.saveAll(roles);

    }

}
