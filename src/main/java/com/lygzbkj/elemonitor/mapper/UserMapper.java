package com.lygzbkj.elemonitor.mapper;

import java.util.List;

import com.lygzbkj.elemonitor.data.User;

public interface UserMapper {

	User findById(long id);
	
	List<User> findAll();

	User findByUsername(String username);
	
	void insert(User user);
	
	void update(User user);
	
	void deleteById(long is);
}
