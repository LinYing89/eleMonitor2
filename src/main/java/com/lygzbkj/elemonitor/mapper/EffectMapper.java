package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.Effect;

public interface EffectMapper {

	List<Effect> findByLinkageId(long linkageId);
	
	Effect findById(long id);
	
	void insert(Effect effect);
	
	void update(Effect effect);
	
	void deleteById(long id);
}
