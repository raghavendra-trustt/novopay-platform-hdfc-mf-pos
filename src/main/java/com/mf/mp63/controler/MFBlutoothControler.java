package com.mf.mp63.controler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.mf.mp63.services.BlueToothServicess;

import model.Devices;


@RestController
public class MFBlutoothControler {
	@Autowired
	BlueToothServicess services;
	@RequestMapping("/main_page")
	public ModelAndView loadHomePage()
	{
		return new ModelAndView("main_page.html");
		
	}
	
	
	@RequestMapping("/device_list")
	public List<Devices> searchEnabledBlutooth() throws IOException, InterruptedException, ExecutionException
	{
		System.out.println("searchEnabledBlutooth is called..."); 
			List<Devices> deviceList=services.getAllDevicess();
			 System.out.println("searchEnabledBlutooth..."+deviceList.size()); 
		     return deviceList;
		
	}


	
	@RequestMapping("/disconnect_device")
	public String disconnetDevice()
	{
		return services.disconnectDevice();
	}
	
	@RequestMapping(value = "/connect_device", params = "mAddress", method = RequestMethod.GET)
	public String connetDevice(@RequestParam("mAddress") String mAddress)
	{
		System.out.print("Address: "+ mAddress);
		return services.connectDevice(mAddress);
	}
	
	@RequestMapping(value = "/request_connection_mode", params = { "vendorId", "connectionMode" }, method = RequestMethod.GET)
	public String connectionModeControler(@RequestParam("vendorId") Integer vendorId,@RequestParam("connectionMode") String connectionMode)
	{
		return services.connectionModeService(vendorId, connectionMode);
	}
	
   @RequestMapping(value = "/request_vendor_id", params =  "vendorId", method = RequestMethod.GET)
  public String connectionModeControler(@RequestParam("vendorId") Integer vendorId)
	{
		return services.setVendorIdService(vendorId);
	}
   @RequestMapping(value = "/is_device_connected", method = RequestMethod.GET)
   public String isConnected()
   {
	   try {
	   boolean isConnected=services.isConnected();
	   if(isConnected)
		   return "Device connected";
	   else
		   return "Device not connected";
	   }catch(Exception e)
	   {
		   return "Service not started! Please check the service";
	   }
   }
	
	
	

}
