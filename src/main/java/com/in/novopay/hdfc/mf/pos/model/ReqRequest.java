package com.in.novopay.hdfc.mf.pos.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ReqRequest implements Serializable {
    private String transaction_type;
    private String handle_type;
    private String handle_value;
    private String iin;
    private String pos_entry_mode;
    private String pos_code;
    private String amount;
    private String cust_card_number;
    private String pinblock;
    private String track2data;
    private String is_emv;
    private String card_seqno;
    private String servicecondcode;
    private String iccdata;
    private String posdata_code;
    private String device_id;
    private String agent_code;
    private String username;
    private String ts;
    private String client_reference_number;
    private String agent_corp_id;

}
