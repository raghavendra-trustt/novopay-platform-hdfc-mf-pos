package com.mf.mp63.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConnectDeviceResponse implements Serializable {

    private String posSerialNumber;
    private ResponseStatus responseStatus;
    private boolean keysRequired;
}
