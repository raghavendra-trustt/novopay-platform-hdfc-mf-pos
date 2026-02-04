package com.mf.mp63.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceKeys implements Serializable {
    private String tmk;
    private String tdk;
    private String tpk;
    private String dtmk;
}
