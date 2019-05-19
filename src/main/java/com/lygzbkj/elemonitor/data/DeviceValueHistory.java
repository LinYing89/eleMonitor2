package com.lygzbkj.elemonitor.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 设备历史纪录
 * @author 44489
 *
 */
public class DeviceValueHistory implements Comparable<DeviceValueHistory>{

	private long id;
	
	private Date time;
	
	private float value;
	
	private Long deviceId;
	private String deviceName = "";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public String timeStr() {
		return new SimpleDateFormat("yy-MM-dd\nHH:mm:ss").format(time);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		if(null == deviceName) {
			deviceName = "?";
		}
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public int compareTo(DeviceValueHistory o) {
		if (o == null) {
			return -1;
		}
		return this.time.compareTo(o.time);
	}
}
