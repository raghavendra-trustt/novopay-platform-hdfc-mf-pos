package com.in.novopay.mf.hdfc;

import bluetooth.RemoteDeviceDiscovery;
import model.Devices;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        //RemoteDeviceDiscovery.runDiscovery();
        Boolean descoveryStatus=RemoteDeviceDiscovery.runDiscovery();
        if(descoveryStatus)
        {
            ExecutorService threadpool = Executors.newCachedThreadPool();
            Future<List<Devices>> futureTask = threadpool.submit(() ->getAllDevice());
            while (!futureTask.isDone()) {
                System.out.println("FutureTask is not finished yet...");
            }
            List<Devices> result = futureTask.get();

            threadpool.shutdown();
            //return result;
            System.out.println(result);
        }
    }

    public static List<Devices> getAllDevice() throws IOException

    {
        Set<RemoteDevice> devices = RemoteDeviceDiscovery.getDevices();
        List<Devices> deviceList= new ArrayList<Devices>();//<Devices>();
        Iterator<RemoteDevice> it = devices.iterator();
        while (it.hasNext()) {
            Devices devicesData=new Devices();
            RemoteDevice device = it.next();

            System.out.println("device name :" + device.getFriendlyName(false));
            devicesData.setName(device.getFriendlyName(false));
            devicesData.setAddress(device.getBluetoothAddress());
            deviceList.add(devicesData);


        }
        return deviceList;

    }
}