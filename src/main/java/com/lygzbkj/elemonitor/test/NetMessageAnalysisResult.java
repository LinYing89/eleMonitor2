package com.lygzbkj.elemonitor.test;

import java.util.ArrayList;
import java.util.List;

public class NetMessageAnalysisResult {

	private String time = "";
	private String data = "";

	private List<OneMessageAnalysisResult> listErrorResult = new ArrayList<>();

	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public List<OneMessageAnalysisResult> getListErrorResult() {
		return listErrorResult;
	}
	public void setListErrorResult(List<OneMessageAnalysisResult> listErrorResult) {
		this.listErrorResult = listErrorResult;
	}
	
	public void addErrResult(OneMessageAnalysisResult err) {
		listErrorResult.add(err);
	}
}
