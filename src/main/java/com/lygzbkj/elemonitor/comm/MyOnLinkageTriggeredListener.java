package com.lygzbkj.elemonitor.comm;

import java.util.List;

import com.lygzbkj.elemonitor.SpringUtil;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.Device.OnLinkageTriggeredListener;
import com.lygzbkj.elemonitor.data.Effect;
import com.lygzbkj.elemonitor.test.SendService;

/**
 * 连锁触发监听
 * @author 44489
 *
 */
public class MyOnLinkageTriggeredListener implements OnLinkageTriggeredListener {

	private SendService sendService = SpringUtil.getBean(SendService.class);
	
	@Override
	public void onLinkageTriggered(Device device, List<Effect> listEffect) {
		for(Effect effect : listEffect) {
			if(effect.getDevice().getValue() != effect.getValue()) {
				sendService.ctrlDev(effect.getDevice(), (int)effect.getValue());
			}
		}
	}

}
