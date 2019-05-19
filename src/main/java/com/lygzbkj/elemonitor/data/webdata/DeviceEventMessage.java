package com.lygzbkj.elemonitor.data.webdata;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lygzbkj.elemonitor.data.Device;

/**
 * 设备事件信息
 * @author 44489
 *
 */
public class DeviceEventMessage implements Comparable<DeviceEventMessage>{

	private long id;
	
//	@JsonBackReference("device_event")
	@JsonIgnore
	private Device device;
	
	private Date eventTime;
	
	private String message;
	
	@JsonIgnore
	private boolean alarm;

	@JsonIgnore
	private String timeFormat;
	
	@JsonIgnore
	private SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yy-MM-dd HH-mm-ss");
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	public String getTimeFormat() {
		if(null == timeFormat || timeFormat.isEmpty()) {
			this.timeFormat = SimpleDateFormat.format(eventTime);
		}
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	@Override
	public int compareTo(DeviceEventMessage o) {
		if (o == null) {
			return -1;
		}
		return o.eventTime.compareTo(this.eventTime);
	}
	
}
