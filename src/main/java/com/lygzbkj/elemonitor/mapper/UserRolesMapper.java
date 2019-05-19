package com.lygzbkj.elemonitor.mapper;

public interface UserRolesMapper {

	void insert(long userId, long roleId);
	
	void deleteByUserId(long userId);
}
