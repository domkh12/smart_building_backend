package edu.npic.smartBuilding.features.deviceType;

import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.domain.DeviceType;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import edu.npic.smartBuilding.mapper.DeviceTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTypeServiceImpl implements DeviceTypeService{

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceTypeMapper deviceTypeMapper;
    private final DeviceRepository deviceRepository;

    @Override
    public DeviceTypeResponse findById(Integer id) {
        DeviceType deviceType = deviceTypeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device type not found!")
        );
        return deviceTypeMapper.toDeviceTypeResponse(deviceType);
    }

    @Override
    public void delete(Integer id) {
        DeviceType deviceType = deviceTypeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device type not found!")
        );
        List<Device> devices = deviceType.getDevices();
        for (Device device : devices) {
            device.setDeviceType(null);
            deviceRepository.save(device);
        }
        deviceTypeRepository.delete(deviceType);
    }

    @Override
    public DeviceTypeResponse update(Integer id, DeviceTypeRequest deviceTypeRequest) {
        DeviceType deviceType = deviceTypeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device type not found!")
        );
        deviceTypeMapper.updateFromDeviceTypeRequest(deviceTypeRequest, deviceType);
        return deviceTypeMapper.toDeviceTypeResponse(deviceTypeRepository.save(deviceType));
    }

    @Override
    public List<DeviceTypeNameResponse> deviceTypeNames() {
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll();
        return deviceTypes.stream().map(deviceTypeMapper::toDeviceTypeNameResponse).toList();
    }

    @Override
    public DeviceTypeResponse create(DeviceTypeRequest deviceTypeRequest) {

        if(deviceTypeRepository.existsByName(deviceTypeRequest.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Device type already exists");
        }

        DeviceType deviceType = deviceTypeMapper.fromDeviceTypeRequest(deviceTypeRequest);
        deviceType.setUuid(UUID.randomUUID().toString());
        deviceType.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType);
        return deviceTypeMapper.toDeviceTypeResponse(deviceType);
    }

    @Override
    public List<DeviceTypeResponse> findAll() {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll(sort);

        return deviceTypes.stream().map(deviceTypeMapper::toDeviceTypeResponse).toList();
    }
}
