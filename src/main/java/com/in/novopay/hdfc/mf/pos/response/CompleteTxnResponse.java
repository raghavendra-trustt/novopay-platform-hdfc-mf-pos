package com.in.novopay.hdfc.mf.pos.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompleteTxnResponse implements Serializable {
    private ResponseStatus responseStatus;
}
