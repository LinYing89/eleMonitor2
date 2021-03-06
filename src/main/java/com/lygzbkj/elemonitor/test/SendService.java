package com.lygzbkj.elemonitor.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.RabbitConfig;
import com.lygzbkj.elemonitor.Util;
import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.Effect;
import com.lygzbkj.elemonitor.data.MsgManager;

@Service
public class SendService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void ctrlEffectDevice(List<Effect> listEffect) {
		if (listEffect.isEmpty()) {
			return;
		}
		//如果状态一致不需要发送
		List<Effect> listResultEffect = new ArrayList<>();
		for (Effect effect : listEffect) {
			if((int)effect.getDevice().getValue() != (int)effect.getValue()) {
				listResultEffect.add(effect);
			}
		}
		if (listResultEffect.isEmpty()) {
			return;
		}
		
		MsgManager msgManager = listResultEffect.get(0).getDevice().getCollector().getMsgManager();
		byte[] byManagerCode = initMsgManagerCodeBytes(msgManager);

		List<byte[]> listOneDeviceBytes = new ArrayList<>();
		for (Effect effect : listResultEffect) {
			byte[] byOne = initOneDeviceCtrlBytes(effect.getDevice(), (int) effect.getValue());
			listOneDeviceBytes.add(byOne);
		}

		byte[] byTotal = initCtrlTotalBytes(byManagerCode, listOneDeviceBytes);

		// 发往通信机
		sendMessage(byTotal);
	}

	private byte[] initCtrlTotalBytes(byte[] byManagerCode, List<byte[]> listOneDeviceBytes) {
		// 计算所有设备报文的长度
		int dataLen = 0;
		for (byte[] byOne : listOneDeviceBytes) {
			dataLen += byOne.length;
		}

		byte[] byTotal = new byte[byManagerCode.length + dataLen + 4];
		byTotal[0] = (byte) (dataLen >> 8);
		byTotal[1] = (byte) dataLen;

		int copyPos = 2;
		System.arraycopy(byManagerCode, 0, byTotal, copyPos, byManagerCode.length);
		copyPos += byManagerCode.length;

		for (byte[] byOne : listOneDeviceBytes) {
			System.arraycopy(byOne, 0, byTotal, copyPos, byOne.length);
			copyPos += byOne.length;
		}

		logger.info("ctrl: " + Util.bytesToHexString(byTotal));
		return byTotal;
	}

	public void ctrlDev(Device device, int option) {
		MsgManager msgManager = device.getCollector().getMsgManager();

		byte[] byManagerCode = initMsgManagerCodeBytes(msgManager);

		byte[] byOne = initOneDeviceCtrlBytes(device, option);
		List<byte[]> listOneDeviceBytes = new ArrayList<>();
		listOneDeviceBytes.add(byOne);
		
		byte[] byTotal = initCtrlTotalBytes(byManagerCode, listOneDeviceBytes);
		// 发往通信机
		sendMessage(byTotal);
		
		//----------------老版本, 正确, 用来对着
//		// 报文, 长度为数据长度加上长度字节数(2) + 通信机号长度(4) + 校验码长度(2)
//		int dataLen = byOne.length;
//		byte[] byTotal = new byte[byManagerCode.length + dataLen + 4];
//		byTotal[0] = (byte) (dataLen >> 8);
//		byTotal[1] = (byte) dataLen;
//
//		int copyPos = 2;
//		System.arraycopy(byManagerCode, 0, byTotal, copyPos, byManagerCode.length);
//		copyPos += byManagerCode.length;
//		System.arraycopy(byOne, 0, byTotal, copyPos, byOne.length);
//		// copyPos += byData.length;
//
//		logger.info("ctrl: " + Util.bytesToHexString(byTotal));
//
//		// 发往通信机
//		sendMessage(msgManager.getId(), byTotal);
		//-------------------------------------------------------
	}

	private byte[] initMsgManagerCodeBytes(MsgManager msgManager) {
		byte[] byManagerCode = new byte[4];
		byManagerCode[0] = (byte) (msgManager.getCode() >> 24);
		byManagerCode[1] = (byte) (msgManager.getCode() >> 16);
		byManagerCode[2] = (byte) (msgManager.getCode() >> 8);
		byManagerCode[3] = (byte) (msgManager.getCode());
		return byManagerCode;
	}

	private byte[] initOneDeviceCtrlBytes(Device device, int option) {
		Collector collector = device.getCollector();
		byte[] byOne = new byte[7];
		byOne[0] = (byte) collector.getBusCode();
		byOne[1] = (byte) collector.getCode();
		byOne[2] = 0x5;
		byOne[3] = (byte) (device.getBeginAddress() >> 8);
		byOne[4] = (byte) device.getBeginAddress();
		if (option == 1) {
			byOne[5] = (byte) 0xff;
		}
		return byOne;
	}

	public void configDev(MsgManager msgManager) {
		byte[] byManagerCode = new byte[4];
		byManagerCode[0] = (byte) (msgManager.getCode() >> 24);
		byManagerCode[1] = (byte) (msgManager.getCode() >> 16);
		byManagerCode[2] = (byte) (msgManager.getCode() >> 8);
		byManagerCode[3] = (byte) (msgManager.getCode());

		// 数据长度
		int totalLen = 0;
		byte[] byHead = new byte[7];
		byHead[2] = 0x40;

		byte[] byAllData = new byte[msgManager.getListCollector().size() * 8];
		byHead[6] = (byte) byAllData.length;
		int pos = 0;
		for (Collector collector : msgManager.getListCollector()) {
			byte[] byData = new byte[8];

			byData[0] = (byte) collector.getCode();
			byData[1] = (byte) collector.getFunctionCode();

			byData[2] = (byte) (collector.getBeginAddress() >> 8);
			byData[3] = (byte) collector.getBeginAddress();

			int dataLen = collector.getDataLength();
			// 如果时开关量设备, 要把位数转为字数
//			if(collector.getFunctionCode() == 01 || collector.getFunctionCode() == 02) {
//				int zi = dataLen / 16;
//				if(dataLen % 16 != 0) {
//					zi += 1;
//				}
//				dataLen = zi;
//			}

			byData[4] = (byte) (dataLen >> 8);
			byData[5] = (byte) dataLen;

			byData[6] = (byte) (dataLen * 2);

//			System.arraycopy(byHead, 0, byOne, 0, byHead.length);
			System.arraycopy(byData, 0, byAllData, pos, byData.length);
			pos += 8;
		}
		totalLen = byHead.length + byAllData.length;
		// 报文, 长度为数据长度加上长度字节数(2) + 通信机号长度(4) + 校验码长度(2)
		byte[] byTotal = new byte[totalLen + 8];
		byTotal[0] = (byte) (totalLen >> 8);
		byTotal[1] = (byte) totalLen;

		int copyPos = 2;
		System.arraycopy(byManagerCode, 0, byTotal, copyPos, byManagerCode.length);
		copyPos += byManagerCode.length;
		System.arraycopy(byHead, 0, byTotal, copyPos, byHead.length);
		copyPos += byHead.length;
		System.arraycopy(byAllData, 0, byTotal, copyPos, byAllData.length);

//		for (byte[] by : listBytes) {
//			System.arraycopy(by, 0, byTotal, copyPos, by.length);
//			copyPos += by.length;
//		}
		logger.info("config: " + Util.bytesToHexString(byTotal));

		// 发往通信机
		sendMessage(byTotal);
	}

	private void sendMessage(byte[] byTotal) {
		rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_MANAGER_CONFIG_EXCHAGE_NAME, "com.zhibo.manager.in", byTotal);
	}
}
