package com.mf.mp63.response;

import com.morefun.mpos.sdk.result.ReadCardResult;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class StartTransactionResponse implements Serializable {
    private ResponseStatus responseStatus;
    private String cardSeqNo;
    private String posEntryMode;
    private String pinBlock;
    private String track2data;
    private String iccChipData;
    private String emv;
    private String serviceCondCode;
    private String posDataCode;
    private String posMode;
    private String cardNumber;
    private String cardType;
    private Map<String,String> deviceInfo;


}
