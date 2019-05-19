package com.lygzbkj.elemonitor.test;

import java.util.ArrayList;
import java.util.List;

public class OneMessageAnalysisResult {

	private int code = 0;
	private String data = "";
	private String message = "";
	private String head = "";
	private byte[] managerCodeBytes;
	private long managerCode;
	private List<String> listOne = new ArrayList<>();
	//子报文
	private List<byte[]> listSubOrder = new ArrayList<>();
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public byte[] getManagerCodeBytes() {
		return managerCodeBytes;
	}
	public void setManagerCodeBytes(byte[] managerCodeBytes) {
		this.managerCodeBytes = managerCodeBytes;
	}
	public long getManagerCode() {
		return managerCode;
	}
	public void setManagerCode(long managerCode) {
		this.managerCode = managerCode;
	}
	public List<String> getListOne() {
		return listOne;
	}
	public void setListOne(List<String> listOne) {
		this.listOne = listOne;
	}
	public List<byte[]> getListSubOrder() {
		return listSubOrder;
	}
	public void setListSubOrder(List<byte[]> listSubOrder) {
		this.listSubOrder = listSubOrder;
	}
	
}
