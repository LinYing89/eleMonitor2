package com.lygzbkj.elemonitor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lygzbkj.elemonitor.Util;
import com.lygzbkj.elemonitor.enums.NetMessageResultEnum;
import com.lygzbkj.elemonitor.exception.NetMessageException;

/**
 * 通信管理机
 * 
 * @author 44489
 *
 */
public class MsgManager {

	private long id;

	private int code;

	private String name;

	private long substationId;
	
	@JsonBackReference("substation_msgmanager")
	private Substation substation;

	@JsonManagedReference("msgmanager_collector")
	private List<Collector> listCollector = new ArrayList<Collector>();
	
	//通信机状态
	@JsonIgnore
	private MsgManagerState msgManagerState = MsgManagerState.OFFLINE;
	
	//状态改变监听器集合
	@JsonIgnore
	private Set<OnMsgManagerStateChangedListener> stMsgManagerStateListener = new HashSet<>();;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSubstationId() {
		return substationId;
	}

	public void setSubstationId(long substationId) {
		this.substationId = substationId;
	}

	public Substation getSubstation() {
		return substation;
	}

	public void setSubstation(Substation substation) {
		this.substation = substation;
	}

	public MsgManagerState getMsgManagerState() {
		return msgManagerState;
	}

	public void setMsgManagerState(MsgManagerState msgManagerState) {
		if(this.msgManagerState != msgManagerState) {
			this.msgManagerState = msgManagerState;
			for(OnMsgManagerStateChangedListener listener : stMsgManagerStateListener) {
				listener.onMsgManagerStateChanged(this, msgManagerState);
			}
		}
	}

	public List<Collector> getListCollector() {
		return listCollector;
	}

	public void setListCollector(List<Collector> listCollector) {
		this.listCollector = listCollector;
	}

	public void addCollector(Collector collector) {
		collector.setMsgManagerId(getId());
		collector.setMsgManager(this);
		listCollector.add(collector);
	}

	public void removeCollector(Collector collector) {
		collector.setMsgManager(null);
		listCollector.remove(collector);
	}

	public Collector findCollectorById(long id) {
		for (Collector c : listCollector) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 获取终端集合, 一个通信机下可能配有终端号相同的多个终端
	 * 目的是一个终端下可同时配置数值量和开关量, 不过是分两次配置, 所以配置的两个终端参数,其实对应一个终端实物
	 * 区别则是功能吗不同, 即值类型不同
	 * @param code
	 * @return
	 */
	public List<Collector> findCollectorByCode(int code) {
		List<Collector> list = new ArrayList<>();
		for (Collector c : listCollector) {
			if (c.getCode() == code) {
				list.add(c);
			}
		}
		return list;
	}
	
	public List<Device> findAllDevice() {
		List<Device> listDevice = new ArrayList<>();
		for (Collector c : listCollector) {
			listDevice.addAll(c.getListDevice());
		}
		return listDevice;
	}
	
	public void addMsgManagerStateChangedListener(OnMsgManagerStateChangedListener onMsgManagerStateChangedListener) {
		stMsgManagerStateListener.add(onMsgManagerStateChangedListener);
	}
	
	public void removeMsgManagerStateChangedListener(OnMsgManagerStateChangedListener onMsgManagerStateChangedListener) {
		stMsgManagerStateListener.remove(onMsgManagerStateChangedListener);
	}

	public List<byte[]> handler(byte[] by) throws Exception{

		// 去掉校验码
		byte[] byAllData = Arrays.copyOfRange(by, 0, by.length - 2);

		int index = 0;
		int oneStart = 0;
		List<byte[]> list = new ArrayList<>();

		while (index < byAllData.length) {
			oneStart = index;
			// 总线号,索引0,长度1
			index += 1;
			// 采集终端号,索引1,长度1
			int collectorCode = Util.bytesToInt(new byte[] {byAllData[index]});
			index += 1;
			// 起始编号(地址),索引2,长度2
			//int beginAddress = byAllData[index] << 8 | byAllData[index + 1];
			index += 2;
			// 数据长度,索引4,长度2
			int dataLen = Util.bytesToInt(new byte[] {byAllData[index], byAllData[index + 1]});
//			int dataLen = byAllData[index] << 8 | byAllData[index + 1];
			if(dataLen == 0) {
				throw new NetMessageException(NetMessageResultEnum.DATA_LENGTH_ZERO);
			}
			index += 2;
			// 数据,索引6, 长度dataLen, -1是去掉功能吗
			byte[] byteData = Arrays.copyOfRange(byAllData, index, index + dataLen - 1);
			//值类型, 获取功能吗
			byte[] byValueType = Arrays.copyOfRange(byAllData, index + dataLen - 1, index + dataLen);
			index += dataLen;
//			index += 1;
			
			byte[] byOne = Arrays.copyOfRange(byAllData, oneStart, index);
			list.add(byOne);
			
			List<Collector> listCollector = findCollectorByCode(collectorCode);
//			if(listCollector.isEmpty()) {
//				throw new NetMessageException(NetMessageResultEnum.UNKNOW_COLLECTOR);
//			}
			for(Collector c : listCollector) {
				if(c.getFunctionCode() == byValueType[0]) {
					//判断值类型是否匹配
					c.handler(byteData);
				}
			}
		}
		return list;
	}
	
	public static List<byte[]> analysisEveryOrder(byte[] by) throws Exception{

		// 去掉校验码
		byte[] byAllData = Arrays.copyOfRange(by, 0, by.length - 2);

		int index = 0;
		int oneStart = 0;
		List<byte[]> list = new ArrayList<>();

		while (index < byAllData.length) {
			oneStart = index;
			// 总线号,索引0,长度1
			index += 1;
			// 采集终端号,索引1,长度1
			//byte collectorCode = byAllData[index];
			index += 1;
			// 起始编号(地址),索引2,长度2
			//int beginAddress = byAllData[index] << 8 | byAllData[index + 1];
			index += 2;
			// 数据长度,索引4,长度2
			int dataLen = Util.bytesToInt(new byte[] {byAllData[index], byAllData[index + 1]});
//			int dataLen = byAllData[index] << 8 | byAllData[index + 1];
			if(dataLen == 0) {
				return list;
//				throw new NetMessageException(NetMessageResultEnum.DATA_LENGTH_ZERO);
			}
			index += 2;
			// 数据,索引6, 长度dataLen
			//byte[] byteData = Arrays.copyOfRange(byAllData, index, index + dataLen - 1);
			//值类型
			//byte[] byValueType = Arrays.copyOfRange(byAllData, index + dataLen - 1, index + dataLen);
			index += dataLen;
//			index += 1;
			
			byte[] byOne = Arrays.copyOfRange(byAllData, oneStart, index);
			list.add(byOne);
		}
		return list;
	}
	
	/**
	 * 通信机状态改变监听器
	 * @author 44489
	 *
	 */
	public interface OnMsgManagerStateChangedListener{
		void onMsgManagerStateChanged(MsgManager msgManager, MsgManagerState state);
	}
}
