package com.mf.mp63.controler;


import com.mf.mp63.request.CompleteTxnRequest;
import com.mf.mp63.request.ConnectDeviceRequest;
import com.mf.mp63.request.StartTransactionRequest;
import com.mf.mp63.response.CompleteTxnResponse;
import com.mf.mp63.response.ConnectDeviceResponse;
import com.mf.mp63.response.DeviceListResponse;
import com.mf.mp63.response.StartTransactionResponse;
import com.mf.mp63.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/txn")
@Slf4j
public class CardController {

    @Autowired
    CardService cardService;

    @GetMapping("/deviceList")
    public DeviceListResponse searchEnabledBluetooth() throws IOException, InterruptedException, ExecutionException
    {
        log.debug("searchEnabledBluetooth is called...");
        return cardService.fetchDeviceNames();

    }

    @PostMapping(path= "/connectUsb",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ConnectDeviceResponse getUsbNames(@RequestBody ConnectDeviceRequest connectDeviceRequest){
        //ObjectMapper mapper = new ObjectMapper();
        //ConnectDeviceRequest usbRequest = null;
        /*try {
            usbRequest = mapper.readValue(request, ConnectDeviceRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        return cardService.establishConnection(connectDeviceRequest);
    }

    @PostMapping(path= "/startTxn",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StartTransactionResponse startTransaction(@RequestBody StartTransactionRequest txnDetails){
        /*ObjectMapper mapper = new ObjectMapper();
        StartTransactionRequest txnDetails = null;
        try {
            txnDetails = mapper.readValue(request, StartTransactionRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        return cardService.startTransaction(txnDetails);
    }

    @PostMapping(path= "/completeTxn",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompleteTxnResponse completeTransaction(@RequestBody CompleteTxnRequest txnDetails){
        /*ObjectMapper mapper = new ObjectMapper();
        StartTransactionRequest txnDetails = null;
        try {
            txnDetails = mapper.readValue(request, StartTransactionRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        return cardService.completeTransaction(txnDetails);
    }
}
