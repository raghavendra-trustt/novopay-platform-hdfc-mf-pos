package com.in.novopay.hdfc.mf.pos.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.novopay.hdfc.mf.pos.services.OtherService;

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
