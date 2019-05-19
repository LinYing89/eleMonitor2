package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.SysRole;

public interface SysRoleMapper {

	SysRole findById(long id);
	
	List<SysRole> findByUserId(long userId);
}
