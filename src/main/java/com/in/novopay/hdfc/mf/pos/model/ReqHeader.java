package com.in.novopay.hdfc.mf.pos.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqHeader implements Serializable {

    private String actor_type;
    private String operation_mode;
    private String channel_code;
    private String function_sub_code;
    private String function_code;
    private String user_id;
    private String end_channel_code;
    private String run_mode;
    private String retry_count;
    private String stan;
    private String user_handle_value;
    private String client_ip;
    private String location;
    private String user_handle_type;
    private String tenant_code;
    private String client_code;
    private String transmission_datetime;
    private String app_version;
    private String platform;
}
