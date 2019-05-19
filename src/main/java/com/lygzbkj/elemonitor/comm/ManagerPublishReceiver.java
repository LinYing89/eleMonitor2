package com.lygzbkj.elemonitor.comm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lygzbkj.elemonitor.Util;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.Effect;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.data.MsgManagerState;
import com.lygzbkj.elemonitor.service.MsgManagerService;
import com.lygzbkj.elemonitor.test.NetMessageAnalysisResult;
import com.lygzbkj.elemonitor.test.OneMessageAnalysisResult;
import com.lygzbkj.elemonitor.test.SendService;

@Component
public class ManagerPublishReceiver {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private MsgManagerService msgManagerService;
	@Autowired
	private SendService sendService;
	
	private MessageAnalysiser messageAnalysiser;
	
	public ManagerPublishReceiver() {
		messageAnalysiser = new MessageAnalysiser();
	}
	
	public void receiveMsg(byte[] msg) {
	    //发给邢老师
	    MyClient.getIns().send(msg);
	    
		String str = Util.bytesToHexString(msg);
		logger.info(str);
		// 长度不可小于8个字节
		if (msg.length < 8) {
			return;
		}
		
		analysisMsg(msg);
	}
	
	public void analysisMsg(byte[] msg) {
		NetMessageAnalysisResult result = messageAnalysiser.analyisis(msg);
		for(OneMessageAnalysisResult oneMsg : result.getListErrorResult()) {
			MsgManager mm = msgManagerService.findByCode((int)oneMsg.getManagerCode());
			if(null == mm) {
				logger.error("通信管理机不存在: code - " + oneMsg.getManagerCode());
				return;
			}
			mm.setMsgManagerState(MsgManagerState.SUCCESS);
			
			for(byte[] subOrder : oneMsg.getListSubOrder()) {
				try {
					mm.handler(subOrder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//查看触发的连锁
			//处理完报文再查看那些连锁触发了, 是为了将同一通信机的设备报文合并为一条报文, 防止一次发送多条数据
			List<Effect> listTriggeredEffect = new ArrayList<>();
			for (Device dev : mm.findAllDevice()) {
				listTriggeredEffect.addAll(dev.getListTriggedEffect());
			}
			sendEffect(listTriggeredEffect);
		}
	}
	
	private void sendEffect(List<Effect> listEffect) {
		sendService.ctrlEffectDevice(listEffect);
	}
}
