package com.lygzbkj.elemonitor.comm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.webdata.StationStateData;
import com.lygzbkj.elemonitor.enums.StationState;

@Service
public class WebBroadcast {

	private SimpMessageSendingOperations messaging;
	
	@Autowired
	public WebBroadcast(SimpMessageSendingOperations messaging) {
		this.messaging = messaging;
	}
	
	public void broadcastStationStateChanged(long stationId, String stationName, StationState state) {
		String topic = "/topic/admin/stationState";
		StationStateData stationStateData = new StationStateData(stationId, stationName, state.getCode(), state.getMessage());
		
		messaging.convertAndSend(topic, stationStateData);
	}
}
