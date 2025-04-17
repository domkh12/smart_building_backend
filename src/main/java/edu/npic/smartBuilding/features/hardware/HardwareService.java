package edu.npic.smartBuilding.features.hardware;

import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;

import java.util.List;

public interface HardwareService {
    void createEvent(int deviceId, String value);

    List<DeviceResponseHardware> findDevicesByRoomId(Integer id);
}
