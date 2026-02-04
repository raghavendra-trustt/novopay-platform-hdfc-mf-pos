package com.mf.mp63.request;


import lombok.Data;

@Data
public class ConnectDeviceRequest extends BaseRequest{

    private String deviceName;
    private String deviceAddress;

}
