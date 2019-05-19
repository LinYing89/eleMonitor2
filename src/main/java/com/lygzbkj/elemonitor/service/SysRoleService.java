package com.lygzbkj.elemonitor.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.SysRole;
import com.lygzbkj.elemonitor.mapper.SysRoleMapper;


@Service
public class SysRoleService {

	@Resource
	private SysRoleService self;
	
	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	public List<SysRole> findByUserId(long userId){
		List<SysRole> list = sysRoleMapper.findByUserId(userId);
		return list;
	}
}
