package com.lygzbkj.elemonitor.data;

import java.util.List;

import javax.validation.constraints.NotNull;

public class SysRole {

    private Long id;
    @NotNull
    private String name;
    private String description;
    private List<SysPermission> permissions;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<SysPermission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<SysPermission> permissions) {
		this.permissions = permissions;
	}
}
