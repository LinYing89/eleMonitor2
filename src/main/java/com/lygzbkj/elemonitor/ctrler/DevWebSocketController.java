package com.lygzbkj.elemonitor.ctrler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.data.webdata.DevCtrlData;
import com.lygzbkj.elemonitor.service.DeviceService;
import com.lygzbkj.elemonitor.service.MsgManagerService;
import com.lygzbkj.elemonitor.test.SendService;

@Controller
public class DevWebSocketController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MsgManagerService msgManagerService;

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private SendService sendService;

	/**
	 * 网页发出的控制命令
	 * 
	 * @param obj
	 */
	@MessageMapping("/ctrlDev")
	public void ctrlDev(DevCtrlData obj) {
		Device dev = deviceService.findById(obj.getDevId());
		sendService.ctrlDev(dev, obj.getOption());
	}

	/**
	 * 网页发出的配置命令
	 * 
	 * @param obj
	 */
	@MessageMapping("/configDev")
	public void configDev(long msgManagerId) {
		logger.info("msgManagerId=" + msgManagerId);
		MsgManager msgManager = msgManagerService.findByMsgManagerId(msgManagerId);

		sendService.configDev(msgManager);
	}
}
