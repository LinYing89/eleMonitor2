package com.lygzbkj.elemonitor.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lygzbkj.elemonitor.enums.StationState;

/**
 * 站房站点
 * 
 * @author 44489
 *
 */
public class Station {

	private long id;

	private String name;
	private String address;
	private double lat;
	private double lng;
	private String tel;
	// 备注
	private String remark;
	// 状态, 0正常, 1离线, 2异常/报警
	private StationState state = StationState.OFFLINE;

	private Date registerTime;

	@JsonManagedReference("station_substation")
	private List<Substation> listSubstation = new ArrayList<>();

	private OnStateChangedListener onStateChangedListener;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public List<Substation> getListSubstation() {
		return listSubstation;
	}

	public void setListSubstation(List<Substation> listSubstation) {
		this.listSubstation = listSubstation;
	}

	public StationState getState() {
		return state;
	}

	public void setState(StationState state) {
		if (this.state != state) {
			this.state = state;
			if (null != onStateChangedListener) {
				onStateChangedListener.onStateChanged(this, state);
			}
		}
	}

	public void refreshState() {
		// substation有一个报警, 则station为报警
		// 否则substation有一个离线, 则station为离线
		// 否则station为正常
		boolean setted = false;
		for (Substation substation : listSubstation) {
			StationState ss = substation.refreshState();

			if (ss == StationState.ALARM) {
				setState(StationState.ALARM);
				return;
			} else if (ss == StationState.OFFLINE) {
				setState(StationState.OFFLINE);
				return;
			} else if (ss != StationState.UNSET && !setted) {
				// 如果不是报警和离线, 也不是未配置, 则为已配置状态, 如果有一个变电站配置了, 则站点为配置状态
				setted = true;
			}

		}
		// 如果所有变电站都没有配置, 则站点为未配置状态
		if (!setted) {
			setState(StationState.UNSET);
			return;
		}
		setState(StationState.NORMAL);
	}

	public OnStateChangedListener getOnStateChangedListener() {
		return onStateChangedListener;
	}

	public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
		this.onStateChangedListener = onStateChangedListener;
	}

	public Substation addSubstation(Substation substation) {
		if (null == substation) {
			return null;
		}
		if (!listSubstation.contains(substation)) {
			substation.setStationId(getId());
			substation.setStation(this);
			listSubstation.add(substation);
		}
		return substation;
	}

	public Substation removeSubstation(Substation substation) {
		if (null == substation) {
			return null;
		}
		listSubstation.remove(substation);
		substation.setStation(null);
		return substation;
	}

	public interface OnStateChangedListener {
		void onStateChanged(Station station, StationState state);
	}
}
