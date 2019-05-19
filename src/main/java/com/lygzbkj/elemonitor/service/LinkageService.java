package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.Effect;
import com.lygzbkj.elemonitor.data.Linkage;
import com.lygzbkj.elemonitor.mapper.LinkageMapper;

@Service
public class LinkageService {
	@Autowired
	private LinkageService self;
	@Autowired
	private LinkageMapper linkageRepository;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private EffectService effectService;
	
	public List<Linkage> findByDeviceId(long deviceId){
		List<Linkage> list = linkageRepository.findByDeviceId(deviceId);
		List<Linkage> list2 = new ArrayList<>();
		for(Linkage ling : list) {
			list2.add(self.findById(ling.getId()));
		}
		return list2;
	}
	
	@Cacheable(value = "linkage", key = "#id")
	public Linkage findById(long id) {
		Linkage d = linkageRepository.findById(id);
		List<Effect> list = effectService.findByLinkageId(id);
		for(Effect e : list) {
			d.addEffect(e);
		}
		return d;
	}
	
	@CachePut(value = "linkage", key = "#result.id")
	public Linkage addDevice(long deviceId, Linkage linkage) {
		Device device = deviceService.findById(deviceId);
		if(null == device) {
			return null;
		}
		device.addLinkage(linkage);
		linkageRepository.insert(linkage);
		return linkage;
	}
	
	public Linkage editDevice(long linkageId, Linkage linkage) {
		Linkage res = self.findById(linkageId);
		if(null != res) {
			res.setAlarming(linkage.isAlarming());
			res.setCompareSymbol(linkage.getCompareSymbol());
			res.setCompareValue(linkage.getCompareValue());
			linkageRepository.update(res);
		}
		return res;
	}
	
	@CacheEvict(value = "linkage", key = "#result.id")
	public Linkage delete(Linkage linkage) {
		linkageRepository.deleteById(linkage.getId());
		return linkage;
	}
}
