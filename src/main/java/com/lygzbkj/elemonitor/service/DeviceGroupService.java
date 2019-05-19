package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.DeviceGroup;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.mapper.DeviceGroupMapper;

@Service
public class DeviceGroupService {

	@Autowired
	private DeviceGroupService self;
	
	@Autowired
	private SubstationService substationService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DeviceGroupMapper deviceGroupRepository;

	public List<DeviceGroup> findBySubstationId(long substationId){
		List<DeviceGroup> list = deviceGroupRepository.findBySubstationId(substationId);
		List<DeviceGroup> list2 = new ArrayList<>();
		for(DeviceGroup d : list) {
			list2.add(self.findById(d.getId()));
		}
		return list2;
	}
	
	@Cacheable(value = "deviceGroup", key = "#devGroupId")
	public DeviceGroup findById(long devGroupId) {
		DeviceGroup dg = deviceGroupRepository.findById(devGroupId);
		return dg;
	}

	@CachePut(value = "deviceGroup", key = "#result.id")
	public DeviceGroup addDeviceGroup(long substationId, DeviceGroup devGroup) {
		Substation substation = substationService.findBySubstationId(substationId);
		if (null == substation) {
			return null;
		}
		// 先找到substation, 建立起对应关系
		substation.addDeviceGroup(devGroup);
		deviceGroupRepository.insert(devGroup);
		return devGroup;
	}

//	@CachePut(value = "deviceGroup", key = "#result.id")
	public DeviceGroup editDeviceGroup(long devGroupId, DeviceGroup devGroup) {
		DeviceGroup res = self.findById(devGroupId);
		if (null != res) {
			res.setName(devGroup.getName());
			res.setValueType(devGroup.getValueType());
			res.setIcon(devGroup.getIcon());
			deviceGroupRepository.update(res);
		}
		return res;
	}

	public void update(DeviceGroup devGroup) {
		deviceGroupRepository.update(devGroup);
	}

	@CacheEvict(value = "deviceGroup", key = "#result.id")
	public DeviceGroup deleteDeviceGroup(DeviceGroup devGroup) {
//		for(Device d : devGroup.getListDevice()) {
//			d.setDeviceGroupId(0);
//			d.setDeviceGroup(null);
//			deviceService.updateDeviceGroupId(d.getId(), 0);
//		}
		deviceGroupRepository.deleteById(devGroup.getId());
		return devGroup;
	}
}
