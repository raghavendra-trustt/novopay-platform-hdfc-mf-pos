package com.in.novopay.hdfc.mf.pos.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompleteTxnRequest extends BaseRequest implements Serializable {

    private String apiStatus;
    private String responseCode;
    private String chipData;
    private String rrn;
    private String txnRefNumber;
    private String commName;
    private String cardType;




}
