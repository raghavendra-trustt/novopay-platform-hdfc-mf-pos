package com.in.novopay.hdfc.mf.pos.controler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in.novopay.hdfc.mf.pos.request.CompleteTxnRequest;
import com.in.novopay.hdfc.mf.pos.request.ConnectDeviceRequest;
import com.in.novopay.hdfc.mf.pos.request.StartTransactionRequest;
import com.in.novopay.hdfc.mf.pos.response.*;
import com.in.novopay.hdfc.mf.pos.service.CardService;
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
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ConnectDeviceResponse getUsbNames(@RequestBody String request){
        ObjectMapper mapper = new ObjectMapper();
        ConnectDeviceRequest connectDeviceRequest = null;
        try {
            connectDeviceRequest = mapper.readValue(request, ConnectDeviceRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cardService.establishConnection(connectDeviceRequest);
    }

    @PostMapping(path= "/startTxn",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StartTransactionResponse startTransaction(@RequestBody String request){
        ObjectMapper mapper = new ObjectMapper();
        StartTransactionRequest txnDetails = null;
        try {
            txnDetails = mapper.readValue(request, StartTransactionRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cardService.startTransaction(txnDetails);
    }

    @PostMapping(path= "/completeTxn",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompleteTxnResponse completeTransaction(@RequestBody String request){
        ObjectMapper mapper = new ObjectMapper();
        CompleteTxnRequest txnDetails = null;
        try {
            txnDetails = mapper.readValue(request, CompleteTxnRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cardService.completeTransaction(txnDetails);
    }

    /**
     * RSA key pair generation
     * @return
     */
    @GetMapping("/cardTxnPbKey")
    public PublickeyResponse getPublicKey(){
        return cardService.getPublicKey();
    }
}
