package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.MsgManager;

public interface MsgManagerMapper {

	List<MsgManager> findBySubstationId(long substationId);
	
	MsgManager findById(long id);
	
	MsgManager findByCode(long code);
	
	long insert(MsgManager msgManager);
	
	void update(MsgManager msgManager);
	
	void deleteById(long msgManagerId);
}
