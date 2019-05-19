package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.Util;
import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.SysRole;
import com.lygzbkj.elemonitor.data.User;
import com.lygzbkj.elemonitor.mapper.SysRoleMapper;
import com.lygzbkj.elemonitor.mapper.UserMapper;
import com.lygzbkj.elemonitor.mapper.UserRolesMapper;

@Service
public class UserService {

	@Resource
	private UserService self;
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private SysRoleMapper sysRoleRepo;
	@Autowired
	private UserRolesMapper userRolesMapper;
	@Autowired
	private StationService stationService;
	
	@Cacheable(value="user", key="#username")
	public User findByUsername(String username) {
		User user = userMapper.findByUsername(username);
		if(null != user) {
			List<SysRole> listRole = sysRoleRepo.findByUserId(user.getId());
			user.setRoles(listRole);
		}
		return user;
	}
	
	public List<User> findAll() {
		List<User> list = userMapper.findAll();
		List<User> list2 = new ArrayList<User>();
		for(User u : list) {
			User u2 = self.findByUsername(u.getUsername());
			list2.add(u2);
		}
		return list2;
	}
	
	@CachePut(value="user",key="#result.username")
	public User addUser(User user) {
		user.setCreateTime(new Date());
		
		String encodedPsd = Util.encodePassword(user.getPassword());
		user.setPassword(encodedPsd);
		userMapper.insert(user);
		
		long roleId = user.getRoles().get(0).getId();
		setRole(user, roleId);
		if(user.getRoles().get(0).getName().equals("ROLE_ADMIN")) {
			user.getListStation().clear();
		}else {
			long stationId = user.getListStation().get(0).getId();
			setStation(user, stationId);
		}
		return user;
	}
	
	public void editUser(User user) {
		User userDb = self.findByUsername(user.getUsername());
		if(null != userDb) {
			if(userDb.getRoles().isEmpty() || user.getRoles().get(0).getId() != userDb.getRoles().get(0).getId()) {
				long roleId = user.getRoles().get(0).getId();
				setRole(userDb, roleId);
			}
			if(userDb.getListStation().isEmpty() || user.getListStation().get(0).getId() != userDb.getListStation().get(0).getId()) {
				long stationId = user.getListStation().get(0).getId();
				setStation(userDb, stationId);
			}
			userDb.setPersonName(user.getPersonName());
			userDb.setTel(user.getTel());
			userDb.setEnable(user.isEnable());
			userDb.setRemark(user.getRemark());
			userMapper.update(userDb);
		}
	}
	
	@CacheEvict(value="user", condition="#result!=null", key="#result.username")
	public void deleteUser(long userId) {
		userRolesMapper.deleteByUserId(userId);
		userMapper.deleteById(userId);
	}
	
	private void setRole(User user, long roleId) {
		SysRole role = sysRoleRepo.findById(roleId);
		if(null != role) {
			if(!user.getRoles().isEmpty()) {
				user.getRoles().clear();
			}
			user.getRoles().add(role);
		}
		userRolesMapper.insert(user.getId(), roleId);
	}
	
	private void setStation(User user, long stationId) {
		Station station = stationService.findByIdNoCache(stationId);
		if(!user.getListStation().isEmpty()) {
			user.getListStation().clear();
		}
		user.addStation(station);
	}
	
}
