package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.mapper.StationMapper;

@Service
public class StationService {

	@Resource
	private StationService self;
	@Autowired
	private StationMapper stationRepository;
	@Autowired
	private SubstationService substationService;
//	@Autowired
//	private CacheManager cacheManager;

	/**
	 * 获取一个站对象
	 * 
	 * @param stationId
	 * @return
	 */
	@Cacheable(value = "station", key = "#stationId")
	public Station findStation(long stationId) {
		Station station = stationRepository.findById(stationId);
		if (null == station) {
			return null;
		}
		return station;
	}
	
	public Station findByIdNoCache(long id) {
		Station station = stationRepository.findById(id);
		return station;
	}

//	public List<Station> findAllByUserId(long userId) {
//		List<Station> listDb = stationRepository.findAllByUserId(userId);
//		List<Station> list = new ArrayList<Station>();
//		for (Station station : listDb) {
//			Station s = self.findStation(station.getId());
//			if (null != s) {
//				list.add(s);
//			}
//		}
//		return list;
//	}

	public List<Station> findAll() {
		List<Station> listDb = stationRepository.findAll();
		List<Station> list = new ArrayList<Station>();
		for (Station station : listDb) {
			Station s = self.findStation(station.getId());
			if (null != s) {
				list.add(s);
			}
		}
		return list;
	}

	@CachePut(value = "station", key = "#result.id")
	public Station save(Station station) {
		stationRepository.save(station);
		return station;
	}

//	@CachePut(value = "station",key="#result.id")
	public Station edit(long stationId, Station station) {
		Station res = self.findStation(stationId);
		if (null != res) {
			res.setName(station.getName());
			res.setAddress(station.getAddress());
			res.setLat(station.getLat());
			res.setLng(station.getLng());
			res.setTel(station.getTel());
			res.setRemark(station.getRemark());
			stationRepository.update(res);
		}
		return res;
	}

	@CacheEvict(value = "station", key = "#stationId")
	public void deleteById(long stationId) {
		Station station = self.findStation(stationId);
		for(Substation ss : station.getListSubstation()) {
			substationService.deleteSubstation(ss);
		}
		stationRepository.deleteById(stationId);
	}
}
