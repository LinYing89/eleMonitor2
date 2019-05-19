package com.lygzbkj.elemonitor.data;

/**
 * 用户权限
 * @author 44489
 * 有 ADMIN, USER两种权限
 */
public class UserAuthority {
	
	private long id;
	
	private String username;
	private String authority;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	
}
