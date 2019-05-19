package com.lygzbkj.elemonitor.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * 连锁
 * 
 * @author 44489
 *
 */
public class Linkage {

	private long id;

	private Long deviceId;
	
	@JsonBackReference("device_linkage")
	private Device device;
	// 比较符号, 1==, 0>, 2<
	private int compareSymbol;
	// 比较值
	private float compareValue;
	private boolean alarming;

	@JsonManagedReference("linkage_effect")
	private List<Effect> listEffect = new ArrayList<Effect>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public int getCompareSymbol() {
		return compareSymbol;
	}

	public void setCompareSymbol(int compareSymbol) {
		this.compareSymbol = compareSymbol;
	}

	public float getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(float compareValue) {
		this.compareValue = compareValue;
	}

	public boolean isAlarming() {
		return alarming;
	}

	public void setAlarming(boolean alarming) {
		this.alarming = alarming;
	}

	public List<Effect> getListEffect() {
		return listEffect;
	}

	public void setListEffect(List<Effect> listEffect) {
		this.listEffect = listEffect;
	}
	
	@JsonIgnore
	public String getStrValue() {
		String symbol = "";
		switch(compareSymbol) {
		case 0:
			symbol = ">";
			break;
		case 1:
			symbol = "=";
			break;
		case 2:
			symbol = "<";
			break;
		}
		return symbol + "  " + compareValue;
	}

	public void addEffect(Effect effect) {
		if (null != effect && !listEffect.contains(effect)) {
			effect.setLinkageId(getId());
			effect.setLinkage(this);
			listEffect.add(effect);
		}
	}

	public void removeEffect(Effect effect) {
		listEffect.remove(effect);
	}

	public boolean compareResult() {
		switch (compareSymbol) {
		case 0:
			//大于
			if(device.getValue() > compareValue) {
				return true;
			}
			break;
		case 1:
			//等于
			if(device.getValue() == compareValue) {
				return true;
			}
			break;
		case 2:
			//小于
			if(device.getValue() < compareValue) {
				return true;
			}
			break;
		}
		return false;
	}
}
