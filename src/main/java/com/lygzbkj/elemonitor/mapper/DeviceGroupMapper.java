package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.DeviceGroup;

public interface DeviceGroupMapper {
	
	List<DeviceGroup> findBySubstationId(long substationId);
	
	DeviceGroup findById(long id);
	
	Long insert(DeviceGroup deviceGroup);
	
	void update(DeviceGroup deviceGroup);
	
	void deleteById(long id);
}
