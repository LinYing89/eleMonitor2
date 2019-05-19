package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.SysPermission;

public interface SysPermissionMapper {

	List<SysPermission> findByUsername(String username);
}
