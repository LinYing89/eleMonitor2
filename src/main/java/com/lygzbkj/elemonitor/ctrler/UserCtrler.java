package com.lygzbkj.elemonitor.ctrler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lygzbkj.elemonitor.data.Station;
import com.lygzbkj.elemonitor.data.User;
import com.lygzbkj.elemonitor.service.StationService;
import com.lygzbkj.elemonitor.service.UserService;

@Controller
@RequestMapping("/user")
public class UserCtrler {
	@Autowired
	private UserService userService;
	@Autowired
	private StationService stationService;
	
	@GetMapping("/page/all")
	public String getUsers(Model model) {
		List<Station> listStation = stationService.findAll();
		List<User> list = userService.findAll();
		model.addAttribute("listUser", list);
		model.addAttribute("listStation", listStation);
		return "sysset/users";
	}
	
	@PostMapping("/add")
	public String addUser(@ModelAttribute User user) {
		userService.addUser(user);
		return "redirect:/user/page/all";
	}
	
	@GetMapping("/del/{userId}")
	public String delUser(@PathVariable long userId) {
		userService.deleteUser(userId);
		return "redirect:/user/page/all";
	}
	
	@PostMapping("/edit")
	public String editUser(@ModelAttribute User user) {
		userService.editUser(user);
		return "redirect:/user/page/all";
	}
}
