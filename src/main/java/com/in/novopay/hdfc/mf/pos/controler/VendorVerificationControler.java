package com.in.novopay.hdfc.mf.pos.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.in.novopay.hdfc.mf.pos.services.VendorVerificationService;

@RestController
public class VendorVerificationControler {
	@Autowired
	private VendorVerificationService vendorVerificationService;
	@RequestMapping(value = "/verify_vendor",  method = RequestMethod.POST)
	public String connetDevice(@RequestParam("plainData") String plainData,@RequestParam("signData") String signData)
	{
		System.out.print("signData: "+ signData);
		return vendorVerificationService.verifyVendor(plainData,signData);
	}
	
	@RequestMapping(value="/sign_data",method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
	public String signData(@RequestParam("file") MultipartFile file,@RequestParam("data") String data)
	{
		System.out.print("data: "+ data);
		return vendorVerificationService.encryptByPrivateKey(data,file);
	}
	@RequestMapping(value="/update_firmware",method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
	public String updateFirmware(@RequestParam("file") MultipartFile file)
	{
		System.out.print("data: "+ file);
		return vendorVerificationService.updateFrimWare(file);
	}
}
