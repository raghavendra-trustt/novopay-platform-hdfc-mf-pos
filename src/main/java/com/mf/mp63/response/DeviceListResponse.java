package com.mf.mp63.response;

import lombok.Data;
import model.Devices;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceListResponse implements Serializable {

    private ResponseStatus responseStatus;
    private List<Devices> devicesList;
}
