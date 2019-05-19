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
import com.lygzbkj.elemonitor.mapper.EffectMapper;

@Service
public class EffectService {

	@Autowired
	private EffectService self;
	
	@Autowired
	private EffectMapper effectRepository;
	@Autowired
	private LinkageService linkageService;
	@Autowired
	private DeviceService deviceService;
	
	public List<Effect> findByLinkageId(long linkageId){
		List<Effect> list = effectRepository.findByLinkageId(linkageId);
		List<Effect> list2 = new ArrayList<>();
		for(Effect effect : list) {
			list2.add(self.findById(effect.getId()));
		}
		return list2;
	}
	
	@Cacheable(value = "effect", key = "#id")
	public Effect findById(long id) {
		Effect d = effectRepository.findById(id);
		Device device = deviceService.findById(d.getDeviceId());
		d.setDeviceId(device.getId());
		d.setDevice(device);
		return d;
	}
	
	@CachePut(value = "effect", key = "#result.id")
	public Effect addEffect(long linkageId, long deviceId, float value) {
		Linkage linkage = linkageService.findById(linkageId);
		Device device = deviceService.findById(deviceId);
		if(null == linkage || null == device) {
			return null;
		}
		Effect effect = new Effect();
		effect.setDeviceId(deviceId);
		effect.setDevice(device);
		effect.setValue(value);
		linkage.addEffect(effect);
		effectRepository.insert(effect);
		return effect;
	}
	
	public Effect editEffect(long effectId, long deviceId, float value) {
		Effect res = self.findById(effectId);
		Device device = deviceService.findById(deviceId);
		if(null != res) {
			res.setDeviceId(device.getId());
			res.setDevice(device);
			res.setValue(value);
			effectRepository.update(res);
		}
		return res;
	}
	
	@CacheEvict(value = "effect", key = "#result.id")
	public Effect delete(Effect effect) {
		effectRepository.deleteById(effect.getId());
		return effect;
	}
	
}
