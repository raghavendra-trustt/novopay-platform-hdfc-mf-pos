package com.in.novopay.hdfc.mf.pos.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseStatus implements Serializable {
    private String status;
    private String code;
    private String message;
}

