package com.lygzbkj.elemonitor.comm;

import com.lygzbkj.elemonitor.SpringUtil;
import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.Station.OnStateChangedListener;
import com.lygzbkj.elemonitor.enums.StationState;

public class MyOnStationStateChangedListener implements OnStateChangedListener {

	private WebBroadcast webBroadcast = SpringUtil.getBean(WebBroadcast.class);
	
	@Override
	public void onStateChanged(Station station, StationState state) {
		webBroadcast.broadcastStationStateChanged(station.getId(), station.getName(), state);
	}

}
