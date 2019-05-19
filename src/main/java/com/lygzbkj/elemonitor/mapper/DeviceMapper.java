package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Device;

public interface DeviceMapper {
	
	List<Device> findByCollectorId(long collectorId);
	
	Device findById(long id);
	
	long insert(Device collector);
	
	void update(Device collector);
	
	void updateDeviceGroupId(long deviceId, Long deviceGroupId);
	
	void deleteById(long id);
}
