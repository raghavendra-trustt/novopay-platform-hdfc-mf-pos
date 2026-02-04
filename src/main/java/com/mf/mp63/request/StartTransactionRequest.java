package com.mf.mp63.request;

import com.mf.mp63.model.DeviceKeys;
import lombok.Data;

import java.io.Serializable;

@Data
public class StartTransactionRequest extends BaseRequest implements Serializable {

    private String amount;
    private String txnType;
    private boolean cardOffus;
    private String commName;
    private String agentMobile;
    private String agentPincode;
    private int pinInput;
    private int requiretype;
    private DeviceKeys keys;
}
