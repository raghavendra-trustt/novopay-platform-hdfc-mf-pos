package com.in.novopay.hdfc.mf.pos.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogShipingRequest implements Serializable {

    private ShipLogRequest request;
    private ReqHeader headers;
}
