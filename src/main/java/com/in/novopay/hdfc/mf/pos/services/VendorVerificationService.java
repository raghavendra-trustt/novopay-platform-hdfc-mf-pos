package com.in.novopay.hdfc.mf.pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.in.novopay.hdfc.mf.pos.DeviceHelper;

@Service
public class VendorVerificationService {
	
	@Autowired
	DeviceHelper deviceHelper;
	
	public String encryptByPrivateKey(String plainData,MultipartFile path)
	{
		return deviceHelper.encryptByPrivateKey(plainData, path);
	}
	public String verifyVendor(String plainData,String signData)
	{
		return deviceHelper.verifyVendor(plainData, signData);
	}
	public String updateFrimWare(MultipartFile file) {
		
		 return deviceHelper.updateFirmware(file);
	}

}
