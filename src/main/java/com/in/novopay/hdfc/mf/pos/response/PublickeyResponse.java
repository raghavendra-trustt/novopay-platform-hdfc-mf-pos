package com.in.novopay.hdfc.mf.pos.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class PublickeyResponse implements Serializable {

    private String publicKey;
    private ResponseStatus responseStatus;
}
