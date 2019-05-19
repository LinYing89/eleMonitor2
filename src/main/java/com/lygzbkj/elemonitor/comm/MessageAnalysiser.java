package com.lygzbkj.elemonitor.comm;

import java.util.Arrays;

import com.lygzbkj.elemonitor.CRC16Util;
import com.lygzbkj.elemonitor.Util;
import com.lygzbkj.elemonitor.test.NetMessageAnalysisResult;
import com.lygzbkj.elemonitor.test.OneMessageAnalysisResult;

public class MessageAnalysiser {

	private OnAnalysiserResultListener analysiserResult;
	
	public OnAnalysiserResultListener getAnalysiserResult() {
		return analysiserResult;
	}

	public void setAnalysiserResult(OnAnalysiserResultListener analysiserResult) {
		this.analysiserResult = analysiserResult;
	}

	public NetMessageAnalysisResult analyisis(byte[] byMessage) {
		NetMessageAnalysisResult result = new NetMessageAnalysisResult();
		String strMsg = Util.bytesToHexString(byMessage);
		result.setData(strMsg);
		
		int startOne = 0;
		while(startOne + 1 < byMessage.length) {
			int len = Util.bytesToInt(new byte[] {byMessage[startOne], byMessage[startOne + 1]});
//			int len = req[startOne] << 8 | req[startOne + 1];
			//一个报文的结束位置
			int to = len + 8 + startOne;
			if(to > byMessage.length) {
				//长度不匹配
				OneMessageAnalysisResult r = new OneMessageAnalysisResult();
				//code不为0表示解析错误
				r.setCode(1);
				r.setMessage("长度不匹配");
				result.addErrResult(r);
				analysisResult(result);
				return result;
			}
			byte[] byOne = Arrays.copyOfRange(byMessage, startOne, to);
			//判断校验和
//			boolean crcRes = CRC16Util.checkCRC(byOne);
			boolean crcRes = CRC16Util.checkCRC(byOne);
			if(!crcRes) {
				OneMessageAnalysisResult r = new OneMessageAnalysisResult();
				r.setCode(2);
				r.setMessage("校验和错误");
				result.addErrResult(r);
				analysisResult(result);
				return result;
			}
			
			OneMessageAnalysisResult errResult = analysisOneMsg(byOne);
			result.addErrResult(errResult);
			startOne = to;
		}
		analysisResult(result);
		return result;
	}
	
	private OneMessageAnalysisResult analysisOneMsg(byte[] byOne) {
		OneMessageAnalysisResult errResult = new OneMessageAnalysisResult();
		errResult.setData(Util.bytesToHexString(byOne));
		
		byte[] by = new byte[byOne.length - 6];
		by = Arrays.copyOfRange(byOne, 6, byOne.length);
		byte[] byHead = Arrays.copyOfRange(byOne, 0, 6);
		errResult.setHead(Util.bytesToHexString(byHead));
		byte[] byManagerCode = Arrays.copyOfRange(byHead, 2, 6);
		errResult.setManagerCodeBytes(byManagerCode);
		//通信机编码第一个字节为类别标识, 不参与编码计算
		long managerNum = Util.bytesToInt(new byte[] {byManagerCode[1], byManagerCode[2], byManagerCode[3]});
		errResult.setManagerCode(managerNum);
//		MainDevice md = new MainDevice();
//		md.setCode(managerNum);
		handler(by, errResult);
		errResult.setCode(0);
//		for(Device dev : list) {
//			webBroadcast.broadcastStationStateChanged(dev);
//		}
		return errResult;
	}
	
	public void handler(byte[] by, OneMessageAnalysisResult errResult) {
		if(by.length < 14) {
			return;
		}
		// 去掉校验码
		byte[] byAllData = Arrays.copyOfRange(by, 0, by.length - 2);

		int index = 0;
		int oneStart = 0;

		while (index < byAllData.length) {
			oneStart = index;
			// 次设备号,索引0,长度2
			int deviceCode = Util.bytesToInt(new byte[] { byAllData[index], byAllData[index + 1] });
			index += 2;
			// 数据编址,索引2,长度2
//			int address = Util.bytesToInt(new byte[] { byAllData[index], byAllData[index + 1] });
			index += 2;
			// 数据长度,索引4,长度2
			int len = Util.bytesToInt(new byte[] { byAllData[index], byAllData[index + 1] });
			index += 2;
//			byte[] byData = Arrays.copyOfRange(byAllData, index, index + len);
			
			index += len;
			
			byte[] byOne = Arrays.copyOfRange(byAllData, oneStart, index);
			errResult.getListOne().add(Util.bytesToHexString(byOne));
		}
	}
	
	private void analysisResult(NetMessageAnalysisResult result) {
		if(null != analysiserResult) {
			analysiserResult.onAnalysisResult(result);
		}
	}
	
	public interface OnAnalysiserResultListener {
		void onAnalysisResult(NetMessageAnalysisResult result);
	}
}
