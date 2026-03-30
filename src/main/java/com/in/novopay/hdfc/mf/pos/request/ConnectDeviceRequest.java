package com.in.novopay.hdfc.mf.pos.request;


import lombok.Data;

@Data
public class ConnectDeviceRequest extends BaseRequest{

    private String deviceName;
    private String deviceAddress;
    private String vendorId;

}
