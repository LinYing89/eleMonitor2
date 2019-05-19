package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Linkage;

public interface LinkageMapper {

	List<Linkage> findByDeviceId(long deviceId);
	
	Linkage findById(long id);
	
	void insert(Linkage linkage);
	
	void update(Linkage linkage);
	
	void deleteById(long id);
}
