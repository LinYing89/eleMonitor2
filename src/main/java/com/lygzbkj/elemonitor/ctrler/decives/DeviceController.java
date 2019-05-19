package com.lygzbkj.elemonitor.ctrler.decives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lygzbkj.elemonitor.data.Collector;
import com.lygzbkj.elemonitor.data.Device;
import com.lygzbkj.elemonitor.data.DeviceGroup;
import com.lygzbkj.elemonitor.service.CollectorService;
import com.lygzbkj.elemonitor.service.DeviceService;

@Controller
@RequestMapping("/device")
public class DeviceController {

	@Autowired
	private CollectorService collectorService; 
	@Autowired
	private DeviceService deviceService; 
	
	@GetMapping("/{collectorId}")
	public String getDevices(@PathVariable long collectorId, Model model) {
		Collector collector = collectorService.findById(collectorId);
		model.addAttribute("collector", collector);
		return "device/device";
	}
	
	@PostMapping("/{collectorId}")
	public String addDevice(@PathVariable long collectorId, @ModelAttribute Device device) {
		deviceService.addDevice(collectorId, device);
		return "redirect:/collector/find/" + collectorId;
	}
	
	@PostMapping("/edit/{deviceId}")
	public String editDevice(@PathVariable long deviceId, @ModelAttribute Device device) {
		Device res = deviceService.editDevice(deviceId, device);
		return "redirect:/collector/find/" + res.getCollector().getId();
	}
	
	@GetMapping("/del/{deviceId}")
	public String deleteDevice(@PathVariable long deviceId) {
		Device device = deviceService.findById(deviceId);
		DeviceGroup dg = device.getDeviceGroup();
		if(dg != null) {
			dg.removeDevice(device);
		}
		Collector collector = device.getCollector();
		collector.removeDevice(device);
		
		deviceService.deleteDevice(device);
		return "redirect:/collector/find/" + collector.getId();
	}
}
