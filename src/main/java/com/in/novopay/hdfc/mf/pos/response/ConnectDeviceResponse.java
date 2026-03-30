package com.in.novopay.hdfc.mf.pos.response;

import lombok.Data;

@Data
public class ConnectDeviceResponse extends BaseResponse {

    private String posSerialNumber;
    private boolean keysRequired;
}
