package com.mf.mp63.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mf.mp63.services.OtherService;

import model.DeviceInfo;

@RestController
public class OtherControler {
	@Autowired
	private OtherService otherService;

	@RequestMapping("/set_time")
	public String setTime()
	{
		return otherService.setTime();
	}
	@RequestMapping("/show_text")
	public String showText()
	{
		return otherService.showText();
	}
	@RequestMapping("/cancel")
	public String cancel()
	{
		return otherService.cancel();
	}
	
	@RequestMapping("/device_info")
	public DeviceInfo getDeviceInfo()
	{
		return otherService.getDeviceInfo();
	}
}
