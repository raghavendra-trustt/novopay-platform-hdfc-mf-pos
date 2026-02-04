package com.in.novopay.hdfc.mf.pos.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ShipLogRequest  implements Serializable {

    private List<String> device_logs;
}
