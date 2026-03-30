package com.in.novopay.hdfc.mf.pos.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {
    private ResponseStatus responseStatus;
}
