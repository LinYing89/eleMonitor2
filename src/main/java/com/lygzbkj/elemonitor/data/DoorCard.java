package com.lygzbkj.elemonitor.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * 门卡
 * 限时授权,  几点到几点
 * @author 44489
 *
 */
public class DoorCard {
	private long id;
	
	//使用者名称
	private String username;
	//卡号
	private String cardNum;
	
	//时限开始时间
	private Date limitTimeStart = new Date();
	//时限结束时间
	private Date limitTimeEnd = new Date();
	
	@JsonBackReference("substation_doorCard")
	private Substation substation;
	
	@JsonManagedReference("doorCard_doorAuthority")
	private List<DoorAuthority> listDoorAuthority = new ArrayList<>();
	
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
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public Date getLimitTimeStart() {
		return limitTimeStart;
	}
	public void setLimitTimeStart(Date limitTimeStart) {
		this.limitTimeStart = limitTimeStart;
	}
	public Date getLimitTimeEnd() {
		return limitTimeEnd;
	}
	public void setLimitTimeEnd(Date limitTimeEnd) {
		this.limitTimeEnd = limitTimeEnd;
	}
	public Substation getSubstation() {
		return substation;
	}
	public void setSubstation(Substation substation) {
		this.substation = substation;
	}
	public List<DoorAuthority> getListDoorAuthority() {
		return listDoorAuthority;
	}
	public void setListDoorAuthority(List<DoorAuthority> listDoorAuthority) {
		this.listDoorAuthority = listDoorAuthority;
	}
	
	public void addDoorAuthority(DoorAuthority doorAuthority) {
		doorAuthority.setDoorCard(this);
		listDoorAuthority.add(doorAuthority);
	}
	
	public void removeDoorAuthority(DoorAuthority doorAuthority) {
		doorAuthority.setDoorCard(null);
		listDoorAuthority.remove(doorAuthority);
	}
	
	@JsonIgnore
	public String getLimitTimeStartStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(null == limitTimeStart) {
			limitTimeStart = new Date();
		}
		return format.format(limitTimeStart);
	}
	
	@JsonIgnore
	public String getLimitTimeEndStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(null == limitTimeEnd) {
			limitTimeEnd = new Date();
		}
		return format.format(limitTimeEnd);
	}
	
	@JsonIgnore
	public String getLimitTimeStr() {
		return getLimitTimeStartStr() + " ~ " + getLimitTimeEndStr();
	}
}
