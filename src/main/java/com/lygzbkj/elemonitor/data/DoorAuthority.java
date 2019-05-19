package com.lygzbkj.elemonitor.data;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 门卡权限
 * @author 44489
 *
 */
public class DoorAuthority implements Comparable<DoorAuthority>{

	private long id;
	
	private Device device;
	
	private long deviceId;
	
	private DoorCardAuthority doorCardAuthority = DoorCardAuthority.ENABLE;
	
	@JsonBackReference("doorCard_doorAuthority")
	private DoorCard doorCard;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public DoorCardAuthority getDoorCardAuthority() {
		return doorCardAuthority;
	}

	public void setDoorCardAuthority(DoorCardAuthority doorCardAuthority) {
		this.doorCardAuthority = doorCardAuthority;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DoorCard getDoorCard() {
		return doorCard;
	}

	public void setDoorCard(DoorCard doorCard) {
		this.doorCard = doorCard;
	}

	@Override
	public int compareTo(DoorAuthority o) {
		if(null == o) {
			return -1;
		}
		return this.device.getName().compareTo(o.getDevice().getName());
	}
	
	
}
