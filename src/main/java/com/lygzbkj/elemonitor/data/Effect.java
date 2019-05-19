package com.lygzbkj.elemonitor.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 连锁目标, 受影响者
 * @author 44489
 *
 */
public class Effect {

	private long id;
	private long deviceId;
	//目标设备
	private Device device;
	//连锁后期望的目标值
	private float value;
	
	private Long linkageId;
	
	@JsonBackReference("linkage_effect")
	private Linkage linkage;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public Long getLinkageId() {
		return linkageId;
	}
	public void setLinkageId(Long linkageId) {
		this.linkageId = linkageId;
	}
	public Linkage getLinkage() {
		return linkage;
	}
	public void setLinkage(Linkage linkage) {
		this.linkage = linkage;
	}
	
	@JsonIgnore
	public String getStrValue() {
		if(value == 0) {
			return "关";
		}else {
			return "开";
		}
	}
	
}
