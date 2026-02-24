package com.in.novopay.hdfc.mf.pos.services;

import com.morefun.mpos.sdk.Log;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RemoteDeviceDiscovery {
	public final static Set<RemoteDevice> devicesDiscovered = new HashSet<RemoteDevice>();
	public static Boolean runDiscovery() throws IOException, InterruptedException, ExecutionException {
		ExecutorService threadpool = Executors.newCachedThreadPool();
		Future<Boolean> futureTask = threadpool.submit(() ->findDevices());
		while (!futureTask.isDone()) {
		     //System.out.println("FutureTask is not finished yet...");
		 } 
		 Boolean result = futureTask.get(); 

		 threadpool.shutdown();
		 return result;
		
	}

	private static boolean findDevices() throws IOException, InterruptedException {
		final Object inquiryCompletedEvent = new Object();
		devicesDiscovered.clear();
		DiscoveryListener listener = new DiscoveryListener() {
			public void inquiryCompleted(int discType) {
				Log.d("#" + "Search Finish");
				synchronized (inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}

			@Override
			public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
				devicesDiscovered.add(remoteDevice);
				try {
					Log.d("#find device:" + remoteDevice.getFriendlyName(false));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
				Log.d("#" + "servicesDiscovered");
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
				Log.d("#" + "serviceSearchCompleted");
			}
		};

		synchronized (inquiryCompletedEvent) {
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			if (started) {
				Log.d("#" + "Wait search...");
				inquiryCompletedEvent.wait();
				LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(listener);
				Log.d("#Find device number：" + devicesDiscovered.size());
				//bluetoothSarchListener.onSerachFinish();
				return true;
			}else 
			{
				return false;
			}
		}
	}

	public static Set<RemoteDevice> getDevices() {
		return devicesDiscovered;
	}

}
