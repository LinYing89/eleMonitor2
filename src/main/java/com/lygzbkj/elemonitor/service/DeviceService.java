package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.comm.MyOnLinkageTriggeredListener;
import com.lygzbkj.elemonitor.comm.MyOnValueListener;
import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.Linkage;
import com.lygzbkj.elemonitor.data.Place;
import com.lygzbkj.elemonitor.data.webdata.DevWebData;
import com.lygzbkj.elemonitor.data.webdata.DeviceEventMessage;
import com.lygzbkj.elemonitor.mapper.DeviceMapper;

@Service
public class DeviceService {

	private SimpMessageSendingOperations messaging;
	
	@Resource
	private DeviceService self;
	
	@Autowired
	private DeviceMapper deviceRepository;
	@Autowired
	private LinkageService linkageService;
//	@Autowired
//	private DeviceValueHistoryService deviceValueHistoryService;
	@Autowired
	private CollectorService collectorService;
	
	@Autowired
	public DeviceService(SimpMessageSendingOperations messaging) {
		this.messaging = messaging;
	}
	
	public List<Device> findByCollectorId(long collectorId){
		List<Device> list =  deviceRepository.findByCollectorId(collectorId);
		List<Device> list2 = new ArrayList<>();
		for(Device c : list) {
			list2.add(self.findById(c.getId()));
		}
		return list2;
	}
	
	@Cacheable(value = "device", key = "#deviceId")
	public Device findById(long deviceId) {
		Device d = deviceRepository.findById(deviceId);
		List<Linkage> list = linkageService.findByDeviceId(deviceId);
		for(Linkage link : list) {
			d.addLinkage(link);
		}
		if(d.getOnValueListener() == null) {
			d.setOnValueListener(new MyOnValueListener());
		}
		if(d.getOnLinkageTriggeredListener() == null) {
			d.setOnLinkageTriggeredListener(new MyOnLinkageTriggeredListener());
		}
		return d;
	}
	
	@CachePut(value = "device", key = "#result.id")
	public Device addDevice(long collectorId, Device device) {
		Collector collector = collectorService.findById(collectorId);
		if(null == collector) {
			return null;
		}
		long placeId = device.getPlace().getId();
		Place place = collector.getMsgManager().getSubstation().findByPlaceId(placeId);
		
		device.setPlaceId(place.getId());
		device.setPlace(place);
		
		collector.addDevice(device);
		deviceRepository.insert(device);
		return device;
	}
	
//	@CachePut(value = "device", key = "#result.id")
	public Device editDevice(long deviceId, Device device) {
		Device res = self.findById(deviceId);
		if(null != res) {
			if(!res.getName().equals(device.getName())) {
				res.setName(device.getName());
			}
			long placeId = device.getPlace().getId();
			Place place = res.getCollector().getMsgManager().getSubstation().findByPlaceId(placeId);
			
			res.setPlaceId(place.getId());
			res.setPlace(place);
			res.setBeginAddress(device.getBeginAddress());
			res.setDataLength(device.getDataLength());
			res.setByteOrder(device.getByteOrder());
			res.setValueType(device.getValueType());
			res.setAlarmTriggerValue(device.getAlarmTriggerValue());
			res.setValueFormat(device.getValueFormat());
			res.setCoefficient(device.getCoefficient());
			res.setUnit(device.getUnit());
			res.setDeviceCategory(device.getDeviceCategory());
			res.setIcon(device.getIcon());
			deviceRepository.update(res);
		}
		return res;
	}
	
	public void update(Device device) {
		deviceRepository.update(device);
	}
	
	public void updateDeviceGroupId(long deviceId, Long deviceGroupId) {
		deviceRepository.updateDeviceGroupId(deviceId, deviceGroupId);
	}
	
	@CacheEvict(value = "device", key = "#result.id")
	public Device deleteDevice(Device device) {
//		deviceRepository.deleteById(device.getId());
		deviceRepository.deleteById(device.getId());
		return device;
	}
	
	/**
	 * 向网页发送设备状态
	 * @param substationId
	 * @param devWebData
	 */
	public void broadcastValueChanged(long substationId, DevWebData devWebData) {
		String topic = String.format("/topic/%d/devState", substationId);
//		String topic = "/topic/admin/devState";
		messaging.convertAndSend(topic, devWebData);
	}
	
	public void broadcastEvent(long substationId, DeviceEventMessage event) {
		String topic = String.format("/topic/%d/devEvent", substationId);
//		String topic = "/topic/admin/devEvent";
		messaging.convertAndSend(topic, event);
	}
	
	public void broadcastAdminEvent(DeviceEventMessage event) {
		String topic = String.format("/topic/admin/devEvent");
//		String topic = "/topic/admin/devEvent";
		messaging.convertAndSend(topic, event);
	}
}
