package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Collector;

public interface CollectorMapper {
	
	List<Collector> findByMsgManagerId(long msgManagerId);
	
	Collector findById(long id);
	
	long insert(Collector collector);
	
	void update(Collector collector);
	
	void deleteById(long id);
}
