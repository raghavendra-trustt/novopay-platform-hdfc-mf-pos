package com.in.novopay.hdfc.mf.pos.service;

import com.in.novopay.hdfc.mf.pos.request.CompleteTxnRequest;
import com.in.novopay.hdfc.mf.pos.request.ConnectDeviceRequest;
import com.in.novopay.hdfc.mf.pos.request.PosSetupRequest;
import com.in.novopay.hdfc.mf.pos.request.StartTransactionRequest;
import com.in.novopay.hdfc.mf.pos.response.*;

public interface CardService {

 ConnectDeviceResponse establishConnection(ConnectDeviceRequest connectDeviceRequest);
 DeviceListResponse fetchDeviceNames();
 StartTransactionResponse startTransaction(StartTransactionRequest txnDetails);
 CompleteTxnResponse completeTransaction(CompleteTxnRequest txnDetails);
 PublickeyResponse getPublicKey();
 PosSetUpResponse setPosSetup(PosSetupRequest posSetupRequest);
}
