package com.in.novopay.hdfc.mf.pos.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosSetupRequest extends BaseRequest implements Serializable {

    private String deviceAddress;
    private String merchantId;
    private String merchantName;
    private String merchantLocation;
    private String vendorId;

}
