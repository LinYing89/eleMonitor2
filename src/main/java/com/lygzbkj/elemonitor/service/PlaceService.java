package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Place;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.mapper.PlaceMapper;

@Service
public class PlaceService {
	
	@Resource
	private PlaceService self;
	
	@Autowired
	private PlaceMapper placeRepo;
	@Autowired
	private SubstationService substationService;
	
	public List<Place> findBySubstationId(long substationId){
		List<Place> list = placeRepo.findBySubstationId(substationId);
		List<Place> list2 = new ArrayList<>();
		for(Place p : list) {
			list2.add(self.findById(p.getId()));
		}
		return list2;
	}
	
	@Cacheable(value = "place", key = "#placeId")
	public Place findById(long placeId) {
		// 获取数据库中的对象
		Place c = placeRepo.findById(placeId);
		return c;
	}

	@CachePut(value = "place", key = "#result.id")
	public Place addPlace(long substationId, Place place) {
		Substation substation = substationService.findBySubstationId(substationId);
		substation.addPlace(place);
		placeRepo.insert(place);
		return place;
	}

//	@CachePut(value = "collector", key = "#result.id")
	public Place editPlace(long placeId, Place place) {
		Place placeDb = self.findById(placeId);
		placeDb.setName(place.getName());
		placeRepo.update(placeDb);
		return placeDb;
	}

	@CacheEvict(value = "place", key = "#placeId")
	public void deletePlace(long placeId) {
		placeRepo.deleteById(placeId);
	}
	
}
