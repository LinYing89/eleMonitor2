package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Substation;

public interface SubstationMapper {

	List<Substation> findByStationId(long stationId);
	
	Substation findById(long substationId);
	
	long insert(Substation substation);
	
	void update(Substation substation);
	
	void deleteById(long id);
}
