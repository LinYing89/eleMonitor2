package com.lygzbkj.elemonitor.data.webdata;

import com.lygzbkj.elemonitor.data.ValueType;

public class DevWebEleData extends DevWebData{
	private String phaseNum;
	
	public DevWebEleData() {
		setValueType(ValueType.ELE);
	}
	public String getPhaseNum() {
		return phaseNum;
	}
	public void setPhaseNum(String phaseNum) {
		this.phaseNum = phaseNum;
	}
	
	
}
