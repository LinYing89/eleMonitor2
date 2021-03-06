package com.lygzbkj.elemonitor.ctrler.decives;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.MsgManager;
import com.lygzbkj.elemonitor.data.Substation;
import com.lygzbkj.elemonitor.service.CollectorService;
import com.lygzbkj.elemonitor.service.MsgManagerService;

@Controller
@RequestMapping("/collector")
public class CollectorController {

	@Autowired
	private CollectorService collectorService;
	@Autowired
	private MsgManagerService msgManagerService;
	
	@GetMapping("/find/{collectorId}")
	public String getCollectorDevices(@PathVariable long collectorId, Model model) {
		Collector collector = collectorService.findById(collectorId);
		model.addAttribute("substation", collector.getMsgManager().getSubstation());
		model.addAttribute("msgManager", collector.getMsgManager());
		model.addAttribute("collector", collector);
		return "devices/collector";
	}
	
	@GetMapping("/{msgManagerId}/{collectorId}")
	public String getCollector(@PathVariable long msgManagerId, @PathVariable long collectorId, Model model) {
		MsgManager msgManager = msgManagerService.findByMsgManagerId(msgManagerId);
		List<Collector> listcollector = msgManager.getListCollector();
		model.addAttribute("msgManagerId", msgManagerId);
		model.addAttribute("msgManagerName", msgManager.getName());
		model.addAttribute("listcollector", listcollector);
		if(listcollector.size() > 0) {
			if(collectorId == 0) {
				model.addAttribute("collector", listcollector.get(0));
			}else {
				boolean haved = false;
				for(Collector s : listcollector) {
					if(s.getId() == collectorId) {
						model.addAttribute("collector", s);
						haved = true;
						break;
					}
				}
				if(!haved) {
					model.addAttribute("collector", listcollector.get(0));
				}
			}
		}
		
		return "device/msgManager";
	}
	
	@PostMapping("/add/{msgManagerId}")
	public String addCollector(@PathVariable long msgManagerId, @ModelAttribute Collector collector) {
		Collector res = collectorService.addCollector(msgManagerId, collector);
		Substation substation = res.getMsgManager().getSubstation();
		return "redirect:/msgManager/" + substation.getId() + "/" + msgManagerId;
	}
	
	@PostMapping("/edit/{collectorId}")
	public String editCollector(@PathVariable long collectorId, @ModelAttribute Collector collector) {
		Collector res = collectorService.editCollector(collectorId, collector);
		Substation substation = res.getMsgManager().getSubstation();
		return "redirect:/msgManager/" + substation.getId() + "/" + res.getMsgManager().getId();
	}
	
	@GetMapping("/del/{collectorId}")
	public String deleteCollector(@PathVariable long collectorId, Model model) {
		Collector res = collectorService.deleteCollector(collectorId);
		//下面两行不能放在service中, 因为从manger中移除res后, 返回的res无法获取manager
		MsgManager manager = res.getMsgManager();
		manager.removeCollector(res);
		return "redirect:/msgManager/" + manager.getSubstation().getId() + "/" + manager.getId();
	}
}
