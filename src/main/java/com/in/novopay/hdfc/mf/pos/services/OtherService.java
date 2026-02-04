package com.in.novopay.hdfc.mf.pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.novopay.hdfc.mf.pos.DeviceHelper;

import model.DeviceInfo;

@Service
public class OtherService {

	@Autowired
	DeviceHelper deviceHelper;
	
	public String setTime()
	{
		return deviceHelper.setTime();
	}

	public String showText() {
		
		return deviceHelper.showText();
	}

	public String cancel() {
		
		return deviceHelper.cancel();
	}
	
	public DeviceInfo getDeviceInfo()
	{
		DeviceInfo deviceInfo=new DeviceInfo();
		deviceInfo.setData(deviceHelper.readPosInfo());
		deviceInfo.setResponseMessage("Succees");
		deviceInfo.setStatusCode("200");
		return deviceInfo;
	}
	
}
