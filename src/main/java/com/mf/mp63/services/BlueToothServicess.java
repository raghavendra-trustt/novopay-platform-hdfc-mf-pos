package com.mf.mp63.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.bluetooth.RemoteDevice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mf.mp63.DeviceHelper;
import com.morefun.mpos.sdk.Controler;
import com.morefun.mpos.sdk.constants.EnumConnectMode;

import bluetooth.RemoteDeviceDiscovery;
import model.Devices;

@Service
@Slf4j
public class BlueToothServicess {
	

	@Autowired
	DeviceHelper deviceHelper;
	
 public List<Devices> getAllDevicess() throws IOException, InterruptedException, ExecutionException
 {
	 Boolean descoveryStatus=RemoteDeviceDiscovery.runDiscovery();
	 if(descoveryStatus)
	 {
		 ExecutorService threadpool = Executors.newCachedThreadPool();
			Future<List<Devices>> futureTask = threadpool.submit(() ->getAllDevice());
			while (!futureTask.isDone()) {
			     log.debug("FutureTask is not finished yet...");
			 } 
			 List<Devices> result = futureTask.get(); 

			 threadpool.shutdown();
			 return result;
	 }
	return null;
	
 }
 public List<Devices> getAllDevice() throws IOException

 {
	 Set<RemoteDevice> devices = RemoteDeviceDiscovery.getDevices();
	 List<Devices> deviceList= new ArrayList<Devices>();//<Devices>();
	 Iterator<RemoteDevice> it = devices.iterator();
		while (it.hasNext()) {  
			Devices devicesData=new Devices();
			RemoteDevice device = it.next();  
			
				log.debug(device.getFriendlyName(false));
				devicesData.setName(device.getFriendlyName(false));
				devicesData.setAddress(device.getBluetoothAddress());
				deviceList.add(devicesData);

			
		} 
	return deviceList;
	 
 }
 
 public String disconnectDevice()
 {
	 try {
	 if (deviceHelper.disconnect()) {
			return "Device disconnect success";
		} else {
			return "Device disconnect fail.";
		}
	 }catch(Exception ex)
	 {
		 return ex.getMessage();
	 }
 }
 public String connectDevice(String mAddress)
 {
	 try {
	 if (deviceHelper.connect(mAddress)) {
			return "Device connect success.";
		} else {
			return "Device connect fail.";
		}
	 }catch (Exception e) {
		// TODO: handle exception
		 return e.getMessage();
	}
 }
 
 
 public String connectionModeService(Integer vendorId,String mode)
 {
	 if (mode.equals("Bluetooth")) {
			Controler.getInstance().init(EnumConnectMode.BLUETOOTH, vendorId);
		} else {
			Controler.getInstance().init(EnumConnectMode.HID, vendorId);
		}
	 
	 return "Success";
 }
 
 public String setVendorIdService(Integer vendorId)
 {
	 Controler.getInstance().setManufacturerId(vendorId);
		//vendorId = Integer.parseInt(e.getItem().toString());
	return "Success";
 }
 
 public boolean isConnected() throws Exception 
 {
	 
	   return deviceHelper.isConnected();
	
 }
}
