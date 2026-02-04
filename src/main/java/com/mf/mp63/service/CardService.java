package com.mf.mp63.service;

import com.mf.mp63.request.CompleteTxnRequest;
import com.mf.mp63.request.ConnectDeviceRequest;
import com.mf.mp63.request.StartTransactionRequest;
import com.mf.mp63.response.CompleteTxnResponse;
import com.mf.mp63.response.ConnectDeviceResponse;
import com.mf.mp63.response.DeviceListResponse;
import com.mf.mp63.response.StartTransactionResponse;

public interface CardService {

 ConnectDeviceResponse establishConnection(ConnectDeviceRequest connectDeviceRequest);
 DeviceListResponse fetchDeviceNames();
 StartTransactionResponse startTransaction(StartTransactionRequest txnDetails);
 CompleteTxnResponse completeTransaction(CompleteTxnRequest txnDetails);
}
