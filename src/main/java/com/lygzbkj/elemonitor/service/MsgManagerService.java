package com.lygzbkj.elemonitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.data.MsgManagerState;
import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.data.MsgManager.OnMsgManagerStateChangedListener;
import com.lygzbkj.elemonitor.mapper.MsgManagerMapper;

@Service
public class MsgManagerService {

	@Autowired
	private MsgManagerService self;

	@Autowired
	private SubstationService substationService;
	@Autowired
	private MsgManagerMapper msgManagerRepository;
	@Autowired
	private CollectorService collectorService;
	@Autowired
	private CacheManager cacheManager;

	public MsgManager findByMsgManagerId(long msgManagerId) {
		MsgManager mm = msgManagerRepository.findById(msgManagerId);
		if (null != mm) {
			// 如果可以, 从缓存中获取对象
			return self.findByCode(mm.getCode());
		}
		return mm;
	}

	public List<MsgManager> findBySubstationId(long substationId) {
		List<MsgManager> list = msgManagerRepository.findBySubstationId(substationId);
		List<MsgManager> list2 = new ArrayList<>();
		for (MsgManager mm : list) {
			list2.add(self.findByCode(mm.getCode()));
		}
		return list2;
	}

	@Cacheable(value = "msgmanager", key = "#msgManagerCode")
	public MsgManager findByCode(int msgManagerCode) {
		MsgManager mm = msgManagerRepository.findByCode(msgManagerCode);

		if (mm != null) {
			List<Collector> list = collectorService.findByMsgManagerId(mm.getId());
			for (Collector c : list) {
				mm.addCollector(c);
			}
			mm.addMsgManagerStateChangedListener(onMsgManagerStateChangedListener);
		}
		return mm;
	}

	// 通信机连接状态改变监听器
	private OnMsgManagerStateChangedListener onMsgManagerStateChangedListener = new OnMsgManagerStateChangedListener() {

		@Override
		public void onMsgManagerStateChanged(MsgManager msgManager, MsgManagerState state) {
			if (null != msgManager.getSubstation()) {
				Station s = msgManager.getSubstation().getStation();
				if (null != s) {
					s.refreshState();
				}
			}
		}

	};

	private boolean codeIsHaved(int code) {
		MsgManager mm = self.findByCode(code);
		if (mm == null) {
			return false;
		}
		return true;
	}

	@CachePut(value = "msgmanager", condition = "#result!=null", key = "#result.code")
	public MsgManager addMsgManager(long substationId, MsgManager msgManager) {
		if (codeIsHaved(msgManager.getCode())) {
			// 已有同号的通信机
			return null;
		}

		Substation substation = substationService.findBySubstationId(substationId);
		if (null == substation) {
			return null;
		}
		// 先找到substation, 建立起对应关系
		substation.addMsgManager(msgManager);
		msgManagerRepository.insert(msgManager);
		return msgManager;
	}

	@CachePut(value = "msgmanager", condition = "#result!=null", key = "#result.code")
	public MsgManager editMsgManager(int oldCode, MsgManager msgManager) {
		MsgManager res = self.findByCode(oldCode);
		if (oldCode == msgManager.getCode()) {
			res.setName(msgManager.getName());
			msgManagerRepository.update(res);
		} else {
			if (codeIsHaved(msgManager.getCode())) {
				// 已有同号的通信机
				return null;
			} else {
				res.setName(msgManager.getName());
				res.setCode(msgManager.getCode());
				msgManagerRepository.update(res);
				// 删除旧缓存, 防止保留旧的编号, 导致通信机号重复
				cacheManager.getCache("msgmanager").evict(oldCode);
			}
		}
		return res;
	}

	/**
	 * 删除通信机
	 * 
	 * @param msgManager
	 * @return
	 */
	@CacheEvict(value = "msgmanager", key = "#result.code")
	public MsgManager deleteMsgManager(MsgManager msgManager) {
		for (Collector c : msgManager.getListCollector()) {
			collectorService.deleteCollector(c.getId());
		}
		msgManagerRepository.deleteById(msgManager.getId());
		return msgManager;
	}

}
