package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Station;

public interface StationMapper {

	Station findById(long id);
	
	List<Station> findAll();
	
	long save(Station station);
	
	void update(Station station);
	
	void deleteById(long id);
}
