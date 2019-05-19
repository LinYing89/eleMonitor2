package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.mapper.CollectorMapper;

@Service
public class CollectorService {

	@Resource
	private CollectorService self;

	@Autowired
	private MsgManagerService msgManagerService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private CollectorMapper collectorRepository;
	@Autowired
	private CacheManager cacheManager;
	
	public List<Collector> findByMsgManagerId(long msgManagerId){
		List<Collector> list =  collectorRepository.findByMsgManagerId(msgManagerId);
		List<Collector> list2 = new ArrayList<>();
		for(Collector c : list) {
			list2.add(self.findById(c.getId()));
		}
		return list2;
	}

	@Cacheable(value = "collector", key = "#collectorId")
	public Collector findById(long collectorId) {
		// 获取数据库中的对象
		Collector c = collectorRepository.findById(collectorId);

		if(null != c) {
			List<Device> list = deviceService.findByCollectorId(collectorId);
			for (Device d : list) {
				c.addDevice(d);
			}
		}
		// 初始化Device并缓存
		for (Device d : c.getListDevice()) {
			cacheManager.getCache("device").put(d.getId(), d);
		}
		return c;
	}

	@CachePut(value = "collector", key = "#result.id")
	public Collector addCollector(long msgManagerId, Collector collector) {
		MsgManager msgManager = msgManagerService.findByMsgManagerId(msgManagerId);
		if (null == msgManager) {
			return null;
		}
		// 先找到msgManager, 建立起对应关系
		msgManager.addCollector(collector);
		collectorRepository.insert(collector);
		return collector;
	}

//	@CachePut(value = "collector", key = "#result.id")
	public Collector editCollector(long collectorId, Collector collector) {
		Collector res = self.findById(collectorId);
		if (null != res) {
			res.setName(collector.getName());
			res.setCode(collector.getCode());
			res.setBusCode(collector.getBusCode());
			res.setFunctionCode(collector.getFunctionCode());
			res.setBeginAddress(collector.getBeginAddress());
			res.setDataLength(collector.getDataLength());
			res.setDataType(collector.getDataType());
			collectorRepository.update(res);
		}
		return res;
	}

	/**
	 * 删除采集终端
	 * 
	 * @param collector
	 * @return
	 */
	@CacheEvict(value = "collector", key = "#collectorId")
	public Collector deleteCollector(long collectorId) {
		Collector res = self.findById(collectorId);
		for(Device d : res.getListDevice()) {
			deviceService.deleteDevice(d);
		}
		collectorRepository.deleteById(res.getId());
		return res;
	}
}
