package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Place;

public interface PlaceMapper {

	List<Place> findBySubstationId(long substationId);
	
	Place findById(long id);
	
	long insert(Place place);
	
	void update(Place place);
	
	void deleteById(long placeId);
}
