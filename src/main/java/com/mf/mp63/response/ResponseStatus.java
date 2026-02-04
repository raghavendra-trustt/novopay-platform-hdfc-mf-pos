package com.mf.mp63.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseStatus implements Serializable {
    private String status;
    private String code;
    private String message;
}

