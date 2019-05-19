package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.DeviceGroup;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.data.Place;
import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.mapper.SubstationMapper;

@Service
public class SubstationService {

	@Resource
	private SubstationService self;
	
	@Autowired
	private SubstationMapper substationRepository;
	@Autowired
	private StationService stationService;
	@Autowired
	private MsgManagerService msgManagerService;
	@Autowired
	private PlaceService placeService;
	@Autowired
	private DeviceGroupService deviceGroupService;
//	@Autowired
//	private DeviceEventMessageService deviceEventMessageService;
	
	public List<Substation> findByStationId(long stationId){
		List<Substation> listSubstation = substationRepository.findByStationId(stationId);
		
		//从缓存中获取
		List<Substation> list = new ArrayList<>();
		for(Substation ss : listSubstation) {
			Substation sb = self.findBySubstationId(ss.getId());
			list.add(sb);
		}
		return list;
	}
	
	@Cacheable(value="substation", key="#substationId")
	public Substation findBySubstationId(long substationId){
		Substation s = substationRepository.findById(substationId);
		if(null != s) {
			List<Place> listPlace = placeService.findBySubstationId(substationId);
			for(Place p : listPlace) {
				s.addPlace(p);
			}
			List<DeviceGroup> listDeviceGroup = deviceGroupService.findBySubstationId(substationId);
			for(DeviceGroup dg : listDeviceGroup) {
				s.addDeviceGroup(dg);
			}
			List<MsgManager> list = msgManagerService.findBySubstationId(substationId);
			for(MsgManager mm : list) {
				s.addMsgManager(mm);
				for(Collector c : mm.getListCollector()) {
					for(Device d : c.getListDevice()) {
						DeviceGroup dg = findDeviceGroupById(listDeviceGroup, d.getDeviceGroupId());
						if(null != dg) {
							dg.addDevice(d);
						}
						Place place = findPlaceById(listPlace, d.getPlaceId());
						d.setPlace(place);
					}
				}
			}
		}
		return s;
	}
	
	private DeviceGroup findDeviceGroupById(List<DeviceGroup> listDeviceGroup, long id) {
		for(DeviceGroup dg : listDeviceGroup) {
			if(dg.getId() == id) {
				return dg;
			}
		}
		return null;
	}
	
	private Place findPlaceById(List<Place> listPlace, long id) {
		for(Place p : listPlace) {
			if(p.getId() == id) {
				return p;
			}
		}
		return null;
	}
	
	@CachePut(value="substation",key="#result.id")
	public Substation addSubStation(long stationId, Substation substation) {
		Station station = stationService.findStation(stationId);
		if(null == station) {
			return null;
		}
		//先找到station, 建立起对应关系, 否则substation没有stationid
		substation.setStationId(station.getId());
		station.addSubstation(substation);
		substationRepository.insert(substation);
		return substation;
	}
	
//	@CachePut(value="substation",key="#result.id")
	public Substation editSubStation(long substationId, Substation substation) {
		Substation sub = self.findBySubstationId(substationId);
		if(null != sub) {
			sub.setName(substation.getName());
			substationRepository.update(sub);
		}
		return sub;
	}
	
	/**
	 * 删除变电所
	 * @param substationId
	 * @return
	 */
	@CacheEvict(value="substation", key="#result.id")
	public Substation deleteSubstation(Substation substation) {
		for(MsgManager m : substation.getListMsgManager()) {
			msgManagerService.deleteMsgManager(m);
		}
		for(DeviceGroup dg : substation.getListDeviceGroup()) {
			deviceGroupService.deleteDeviceGroup(dg);
		}
		for(Place p : substation.getListPlace()) {
			placeService.deletePlace(p.getId());
		}
		substationRepository.deleteById(substation.getId());
		return substation;
	}
	
//	public List<DeviceEventMessage> findAllEvent(Substation substation){
//		List<DeviceEventMessage> list = new ArrayList<DeviceEventMessage>();
//		for(Device dev : substation.findDevices()) {
//			list.addAll(deviceEventMessageService.findTodayByDeviceId(dev.getId()));
//		}
//		Collections.sort(list);
//		if(list.size() > 30) {
//			return list.subList(0, 30);
//		}
//		return list;
//	}
}
