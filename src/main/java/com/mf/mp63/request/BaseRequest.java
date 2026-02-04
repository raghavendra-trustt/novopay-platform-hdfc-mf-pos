package com.mf.mp63.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRequest implements Serializable {

    private String captureDeviceLogs;
    private String terminalId;
    private String txnRefId;
    private String userId;
    private String baseUrl;
    private String tenantCode;
}
