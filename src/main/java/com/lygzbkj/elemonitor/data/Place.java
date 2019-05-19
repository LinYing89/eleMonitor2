package com.lygzbkj.elemonitor.data;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 位置信息
 * @author 44489
 *
 */
public class Place {

	private long id;

	private String name;
	private int sortIndex;
	
	private long substationId;
	@JsonBackReference("substation_plcae")
	private Substation substation;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSortIndex() {
		return sortIndex;
	}
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}
	public long getSubstationId() {
		return substationId;
	}
	public void setSubstationId(long substationId) {
		this.substationId = substationId;
	}
	public Substation getSubstation() {
		return substation;
	}
	public void setSubstation(Substation substation) {
		this.substation = substation;
	}
	
}
