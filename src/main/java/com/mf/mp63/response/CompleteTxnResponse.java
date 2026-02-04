package com.mf.mp63.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompleteTxnResponse implements Serializable {
    private ResponseStatus responseStatus;
}
