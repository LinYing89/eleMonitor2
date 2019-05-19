package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.lygzbkj.elemonitor.data.SysPermission;
import com.lygzbkj.elemonitor.mapper.SysPermissionMapper;

@Component
public class MyCustomUserService implements UserDetailsService {

	@Autowired
	private UserService userService;
	@Autowired
	private SysPermissionMapper sysPermissionRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.lygzbkj.elemonitor.data.User myUser = userService.findByUsername(username);
		if (myUser != null) {
			List<SysPermission> permissions = sysPermissionRepo.findByUsername(myUser.getUsername());
			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			for (SysPermission permission : permissions) {
				if (permission != null && permission.getName() != null) {

					GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
					// 1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
					grantedAuthorities.add(grantedAuthority);
				}
			}
			return new User(myUser.getUsername(), myUser.getPassword(), grantedAuthorities);
		} else {
			throw new UsernameNotFoundException("admin: " + username + " do not exist!");
		}

	}

}
