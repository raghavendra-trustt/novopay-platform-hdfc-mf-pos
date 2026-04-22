package com.in.novopay.hdfc.mf.pos.service;

import com.in.novopay.hdfc.mf.pos.listeners.TipListener;
import com.in.novopay.hdfc.mf.pos.request.PosSetupRequest;
import com.in.novopay.hdfc.mf.pos.response.*;
import com.in.novopay.hdfc.mf.pos.util.RSAEcbUtil;
import com.in.novopay.hdfc.mf.pos.util.SecurityUtil;
import com.in.novopay.hdfc.mf.pos.notifications.NotificationService;
import com.in.novopay.hdfc.mf.pos.util.KCVGenerator;
import com.in.novopay.hdfc.mf.pos.DeviceHelper;
import com.in.novopay.hdfc.mf.pos.model.DeviceKeys;
import com.in.novopay.hdfc.mf.pos.request.CompleteTxnRequest;
import com.in.novopay.hdfc.mf.pos.request.ConnectDeviceRequest;
import com.in.novopay.hdfc.mf.pos.request.StartTransactionRequest;
import com.in.novopay.hdfc.mf.pos.services.BlueToothServicess;
import com.in.novopay.hdfc.mf.pos.services.OtherService;
import com.in.novopay.hdfc.mf.pos.util.TripleDESUtil;
import com.morefun.mpos.sdk.Controler;
import com.morefun.mpos.sdk.constants.EnumBitmapLocation;
import com.morefun.mpos.sdk.constants.EnumCommRet;
import com.morefun.mpos.sdk.result.ReadCardResult;
import com.morefun.mpos.sdk.result.ReadPosInfoResult;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utils.BytesUtil;
import utils.TransactionType;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CardServiceImpl implements CardService{

    @Autowired
    BlueToothServicess services;

    @Autowired
    private OtherService otherService;

    @Autowired
    KCVGenerator kcvGenerator;

    @Value("${app.pos.version}")
    String appVersion;

    @Value("${app.pos.date}")
    String appVersionDate;

    @Autowired
    NotificationService notificationService;
    
    @Autowired
    DeviceHelper deviceHelper;

    @Autowired
    RSAEcbUtil rsaUtil;

    @Autowired
    TripleDESUtil tripleDESUtil;

    public static final String MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT = "Morefun device not detected..... please check USB port";

    /**
     * combination of select vendor & set connection mode
     * @param connectDeviceRequest
     * @return
     */

    public ConnectDeviceResponse establishConnection(ConnectDeviceRequest connectDeviceRequest) {
        ConnectDeviceResponse connectDeviceResponse = new ConnectDeviceResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        boolean deviceFlag = deviceHelper.isConnected();
        int defaultVendorId = 28;
        try {
            if(StringUtils.isNotBlank(connectDeviceRequest.getVendorId())) {
                defaultVendorId = Integer.parseInt(connectDeviceRequest.getVendorId());
            }
            //Set Vendor // as confimed by Morefun team, vendor Id was hard code to 0
            String vendorResp = services.setVendorIdService(defaultVendorId);
            if (!"success".equalsIgnoreCase(vendorResp)) {
                return buildFailureResponse(connectDeviceResponse, responseStatus,
                        "Device connect fail - setVendor api failed");
            }

            //Set Connection Mode
            String connectionModeResp = services.connectionModeService(0, "HID");
            if (!"success".equalsIgnoreCase(connectionModeResp)) {
                return buildFailureResponse(connectDeviceResponse, responseStatus,
                        "Device connect fail - setConnectionMode api failed");
            }

            //Connect USB
            boolean connectUsb = deviceHelper.connect(connectDeviceRequest.getDeviceAddress());
            if (!connectUsb) {
                notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
                return buildFailureResponse(connectDeviceResponse, responseStatus,
                        "Device connect fail");
            }

            DeviceInfo deviceInfo = otherService.getDeviceInfo();
            responseStatus.setCode("00");
            responseStatus.setStatus("Success");
            responseStatus.setMessage("Device connect success");
            connectDeviceResponse.setPosSerialNumber(deviceInfo.getData().sn);
            notificationService.showCustomNotification(null, "USB connection established successfully");
        } catch (Exception e) {
            log.error("Error on connect device: ", e);
            notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
            return buildFailureResponse(connectDeviceResponse, responseStatus,
                    "Device connect fail");
        }

        connectDeviceResponse.setResponseStatus(responseStatus);
        return connectDeviceResponse;
    }

    private <T extends BaseResponse> T buildFailureResponse(T response,
                                                            ResponseStatus status,
                                                            String message) {
        status.setCode("120");
        status.setStatus("Failed");
        status.setMessage(message);
        response.setResponseStatus(status);
        return response;
    }


    @Override
    public DeviceListResponse fetchDeviceNames() {
        DeviceListResponse deviceListResponse = new DeviceListResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        List<Devices> deviceList = null;
        try {
            List<Devices> allDevices = services.getAllDevicess();
            deviceList = (allDevices == null || allDevices.isEmpty())
                    ? Collections.emptyList()
                    : allDevices.stream()
                    .filter(s -> s.getName() != null && s.getName().startsWith("MP"))
                    .collect(Collectors.toList());
            if(deviceList!= null && !deviceList.isEmpty()) {
                Map<String, String> deviceInfo = getDeviceInfo();
                responseStatus.setCode("00");
                responseStatus.setStatus("Success");
                responseStatus.setMessage("devices fetched");
                MDC.put("terminalId", deviceInfo.get("MF_SERIAL_NUMBER"));
            } else {
                notificationService.showCustomNotification(null, "Please ensure bluetooth enabled in Desktop/laptop");
                responseStatus.setCode("120");
                responseStatus.setStatus("Failed");
                responseStatus.setMessage("fetch devices failed");
            }

            log.debug("Bluetooth device count : {}",deviceList.size());
        } catch (Exception e) {
            log.error("error on fetch devices : ",e);
            notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
            responseStatus.setCode("120");
            responseStatus.setStatus("Failed");
            responseStatus.setMessage("fetch devices failed");
        } finally {
            deviceListResponse.setDevicesList(deviceList);
            deviceListResponse.setResponseStatus(responseStatus);
        }
        return deviceListResponse;
    }

    public Map<String, String> getDeviceInfo() {
        Map<String, String> deviceInfo = new HashMap<>();
        try {
            DeviceInfo devInfo = otherService.getDeviceInfo();
            ReadPosInfoResult posInfo = devInfo.getData();
            deviceInfo.put("NOVOPAY_UTILITY_JAR_VERSION",appVersion+ "(" + appVersionDate + ")");
            deviceInfo.put("MF_SERIAL_NUMBER", posInfo.sn);
            deviceInfo.put("MF_FIRM_WARE_VERSION", posInfo.posVer);
            deviceInfo.put("MF_MODEL", posInfo.model);
            deviceInfo.put("MF_APP_VERSION", posInfo.dataVer);

        } catch (Exception e) {
            log.error("Error while fetching POS device info. Error message: " + e.getMessage());
        }
        return deviceInfo;
    }

    /**
     * combination of setIndex, load DTMK,TMK,TDK,TPK
     * @param txnDetails
     * @return
     */
    @Override
    public StartTransactionResponse startTransaction(StartTransactionRequest txnDetails) {
        log.info("Initiating Card Transaction");
        StartTransactionResponse cardResponse = new StartTransactionResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        cardResponse.setResponseStatus(responseStatus);
        boolean deviceFlag = deviceHelper.isConnected();
        if(!deviceFlag) {
            boolean connectUsb = deviceHelper.connect(txnDetails.getCommName());
            if (!connectUsb) {
                notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
                responseStatus.setCode("120");
                responseStatus.setStatus("Failed");
                responseStatus.setMessage("Device connect fail");
                cardResponse.setResponseStatus(responseStatus);
                return cardResponse;
            }
        }
        DeviceKeys keys = txnDetails.getKeys();

        if (!injectKeys(keys)) {
            responseStatus.setMessage("Key injection failed");
            responseStatus.setCode("201");
            return cardResponse;
        }

        boolean isEMVParamLoaded = loadEMV();
        if(!isEMVParamLoaded){
            responseStatus.setMessage("EMV param load failed");
            responseStatus.setCode("201");
            return cardResponse;
        }

        /*String loadAid = loadAids();
        if (loadAid == null || loadAid.equalsIgnoreCase("Download aid fail")) {
            responseStatus.setMessage("AID download failed");
            responseStatus.setCode("201");
            return cardResponse;
        }

        String capk = loadCAPKs();
        if (capk == null || capk.equalsIgnoreCase("Download CAPK ERROR")) {
            responseStatus.setMessage("CAPK download failed");
            responseStatus.setCode("201");
            return cardResponse;
        }*/

        TransactionType transactionType = Arrays.stream(TransactionType.values())
                .filter(t -> t.getDCTransactionCategory().equals(txnDetails.getTxnType()))
                .findFirst()
                .orElse(TransactionType.BALANCE_ENQUIRY);

        ReadCardModel param = getReadCardModel(txnDetails, transactionType);
        ReadCardResult result;
        try {
            result = deviceHelper.readCard(param);
        } catch (IllegalAccessException e) {
            log.error("Exception on card read: ", e);
            responseStatus.setMessage("Card read exception");
            responseStatus.setCode("201");
            return cardResponse;
        }

        log.info("Card result : {}", result);

        if (result == null) {
            responseStatus.setMessage("No card result");
            responseStatus.setCode("201");
            return cardResponse;
        }

        String plaintrack2 = result.track2;

        if(StringUtils.isNotBlank(plaintrack2)) {
            cardResponse.setTrack2data(getTrack2Data(keys, plaintrack2));
        }

        if (!EnumCommRet.NOERROR.equals(result.commResult)) {
            responseStatus.setMessage("Read card error");
            responseStatus.setCode("201");
            return cardResponse;
        }

        // Delegate card type handling
        handleCardType(result, cardResponse, responseStatus);
        String txnFunctionCode = "MCR";
        String posDataCode = preparePosDataCode(txnFunctionCode, txnDetails.getAgentMobile(), txnDetails.getAgentPincode());
        cardResponse.setPosDataCode(posDataCode);
        cardResponse.setPosEntryMode("051");
        cardResponse.setPosMode("05");
        cardResponse.setEmv("1");
        cardResponse.setResponseStatus(responseStatus);
        cardResponse.setDeviceInfo(getDeviceInfo());

        return cardResponse;
    }

    private String getTrack2Data(DeviceKeys keys, String plaintrack2) {
        String track2data = null;
        log.info("plain Track2Data: " + plaintrack2);
        try {
            String dtmk = keys.getDtmk();//"E2D16FDED24111FA508DBE9960D1787E"; // 32 hex chars = 16 bytes
            String encTmk = keys.getTmk();//"F190E7A878C74F705FE19C4925CD0EDD"; // Encrypted key
            String plainTmk = tripleDESUtil.decrypt(encTmk, dtmk);
            String encTdk = keys.getTdk();//"8BB5498941B142B43C06E389A65B563B";
            String plainTdk = tripleDESUtil.decrypt(encTdk, plainTmk);
            String encTpk = keys.getTpk();//"5753D0D9FBD20C1B802DCE05793E1183";
            String plainTpk = tripleDESUtil.decrypt(encTpk, plainTmk);
            track2data = tripleDESUtil.encrypt(plaintrack2, plainTdk);
            //log.info("plain TMK:" + plainTmk + " plain TDK:" + plainTdk + " plainTpk: "+ plainTpk+ " encrypted Track2Data: " + track2data);
            //log.info("decrypted Track2Data: " + tripleDESUtil.decrypt(track2data,plainTdk));
        } catch (Exception e) {
            log.error("error while encrypting track2data "+e);
        }
        return track2data;
    }

    private void handleCardType(ReadCardResult result,
                                StartTransactionResponse cardResponse,
                                ResponseStatus responseStatus) {
        switch (result.cardType) {
            case 0:
                responseStatus.setMessage("Canceled by User");
                responseStatus.setCode("201");
                break;
            case 1:
                responseStatus.setMessage("Success");
                responseStatus.setCode("200");
                cardResponse.setCardType("Mag Card");
                setCardResp(result, cardResponse);
                break;
            case 2:
                responseStatus.setMessage("Success");
                responseStatus.setCode("200");
                cardResponse.setCardType("IC Card");
                setCardResp(result, cardResponse);
                break;
            case 3:
                responseStatus.setMessage("Success");
                responseStatus.setCode("200");
                cardResponse.setCardType("RF Card");
                setCardResp(result, cardResponse);
                break;
            case 4:
                responseStatus.setMessage("Need insert IC card");
                responseStatus.setCode("200");
                break;
            case 5:
                responseStatus.setMessage("Read card timeout");
                responseStatus.setCode("201");
                break;
            case 6:
                responseStatus.setMessage("Read card error");
                responseStatus.setCode("201");
                break;
            default:
                responseStatus.setMessage("Unknown card type");
                responseStatus.setCode("201");
                break;
        }
    }

    private void setCardResp(ReadCardResult result,
                        StartTransactionResponse cardResponse){
        cardResponse.setCardNumber(result.pan);
        cardResponse.setCardSeqNo(result.pansn);//always getting 01
        cardResponse.setCardType(String.valueOf(result.cardType));
        cardResponse.setIccChipData(result.icData);
        cardResponse.setPinBlock(result.pinblock);
        cardResponse.setServiceCondCode(result.serviceCode);
        //cardResponse.setTrack2data(result.track2);
    }


    @Override
    public CompleteTxnResponse completeTransaction(CompleteTxnRequest completeTxnRequest) {
        CompleteTxnResponse completeTxnResponse = new CompleteTxnResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        log.info("Completing Transaction");
        try {
            List<String> responseChipData = new ArrayList<>();
            responseChipData.add(completeTxnRequest.getChipData());
            log.info("final responseChipData data : {}", responseChipData);
            String apiStatusCode = completeTxnRequest.getApiStatus();
            String onlineAuthResultString = "";
            //try {
                int responseCode = 2;
                if (apiStatusCode.equalsIgnoreCase("success")) {
                    responseCode = 0;
                } else if (apiStatusCode.equalsIgnoreCase("fail")) {
                    responseCode = 1;
                }
                log.info("setting EMV response card data...");

                if (responseCode == 0) {
                    log.info("Transaction approved online");
                    //transaction approved online
                    onlineAuthResultString = "00";
                } else if (responseCode == 2) {
                    log.info("Connect host failed");
                    //connect host failed
                    onlineAuthResultString = "02";
                } else {
                    log.info("Transaction declined online");
                    //transaction declined online
                    onlineAuthResultString = "01";
                }
                log.info("response code : " + responseCode);
                //responseChipData.add("0308" + String.valueOf(Hex.encodeHex(onlineAuthResultString.getBytes())));
            String authData = "";
            if (!responseChipData.isEmpty()) {
                authData = responseChipData.stream()
                        .filter(Objects::nonNull) // remove nulls
                        .filter(s -> !s.isEmpty()) // remove empty strings
                        .collect(Collectors.joining()); // concatenate
            }
            log.info("authData : {}" , authData);
            String resp = deviceHelper.onlineAuth(authData,onlineAuthResultString);
            if(apiStatusCode.equalsIgnoreCase("success")) {
                notificationService.showCustomNotification("checked.png","Transaction completed successfully.. Please Remove Card");
            } else {
                notificationService.showCustomNotification(null, "Txn Declined.. Please Remove Card");
            }
            //notificationService.showCustomNotification("checked.png", "Please remove the card");
            responseStatus.setCode("00");
            responseStatus.setStatus("Success");
            responseStatus.setMessage(resp);
            completeTxnResponse.setResponseStatus(responseStatus);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            log.error("error on complete transaction {0}", e);
        }
        return completeTxnResponse;
    }

    private static ReadCardModel getReadCardModel(StartTransactionRequest txnDetails, TransactionType transactionType) {
        ReadCardModel param = new ReadCardModel();
        param.setAllowfallback(true);
        //converting to implied decimal since SDK expects
        param.setAmount(getAmountInImpliedDecimals(txnDetails.getAmount()));
        param.setPinInput(txnDetails.getPinInput());
        param.setPinMaxLen((byte)6);
        param.setCardTimeout((byte)60);
        param.setTransName(transactionType.getDCTransactionType());
        param.setRequiretype((byte) txnDetails.getRequiretype());
        param.setEmvTransactionType(transactionType.getDcTxnTypeVal());
        param.setForceonline(true);
        return param;
    }

    private static String getAmountInImpliedDecimals(String amountStr) {
        if (StringUtils.isBlank(amountStr)) {
            return "0";
        }
        // Convert to BigDecimal
        BigDecimal amount = new BigDecimal(amountStr);

        // Multiply by 100 to shift decimal
        BigDecimal implied = amount.multiply(BigDecimal.valueOf(100));

        // Convert to long safely
        long longAmount = implied.longValue();

        return String.valueOf(longAmount);
    }


    private boolean injectKeys(DeviceKeys deviceKeys) {
        // default to false
        boolean keyInjection = false;

        // set index
        KeyIndexModel keyIndexModel = new KeyIndexModel();
        keyIndexModel.setKeyIndex(0);
        String keyIndexResp = deviceHelper.setKeyIndex(keyIndexModel);
        log.info("keyIndexResp : {}", keyIndexResp);

        if (keyIndexResp == null || !keyIndexResp.equalsIgnoreCase("Set key index success")) {
            return false;
        }

        // loadkek(DTMK)
        LoadKekModel loadKekModel = new LoadKekModel();
        loadKekModel.setKey(deviceKeys.getDtmk());
        String data = "0000000000000000";
        byte[] kcv = SecurityUtil.doubleDes(BytesUtil.hexString2ByteArray(deviceKeys.getDtmk()), BytesUtil.hexString2ByteArray(data));
        String dtmkKcv = BytesUtil.bytes2Hex(kcv).substring(0,8);
        loadKekModel.setKcv(dtmkKcv);
        boolean loadDtmkResp = deviceHelper.loadKek(loadKekModel);
        log.info("loadDtmkResp : {}",loadDtmkResp);
        if (!loadDtmkResp) {
            return false;
        }

        // loadkek(TMK)
        String kek = deviceKeys.getDtmk();
        byte[] plain = SecurityUtil.doubleUnDes(BytesUtil.hexString2ByteArray(kek), BytesUtil.hexString2ByteArray(deviceKeys.getTmk()));
        byte[] kvc = SecurityUtil.doubleDes(plain, BytesUtil.hexString2ByteArray(data));

        LoadMasterKeyModel loadMasterKeyModel = new LoadMasterKeyModel();
        String dmkKcvStr = BytesUtil.bytes2Hex(kvc).substring(0,8);
        loadMasterKeyModel.setKey(deviceKeys.getTmk());
        loadMasterKeyModel.setKcv(dmkKcvStr);
        boolean loadTmkResp = deviceHelper.loadMasterKey(loadMasterKeyModel);
        log.info("loadTmkResp : {}", loadTmkResp);

        if (!loadTmkResp) {
            return false;
        }

        // loadkeys(TDK,TPK)
        LoadWorkKeyModel loadWorkKeyModel = new LoadWorkKeyModel();
        loadWorkKeyModel.setPinKey(deviceKeys.getTpk());
        loadWorkKeyModel.setTdkKey(deviceKeys.getTdk());
        boolean loadWorkekyResp = deviceHelper.loadWorkKey(loadWorkKeyModel);
        log.info("loadWorkekyResp : {}", loadWorkekyResp);

        if (!loadWorkekyResp) {
            return false;
        }

        // all steps succeeded
        keyInjection = true;
        return keyInjection;
    }


    private String loadAids(){
        ArrayList<String> aid = new ArrayList<>();
        /*aid.add("9F0608A000000333010100DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607A0000000031010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607A0000000041010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607D4100000012010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607D4100000011010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000025010402DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000025010501DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607A0000000651010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607A0000001523010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0607A0000005241010DF0101009F08020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
        aid.add("9F0608A000000524010101DF0101009F08020030DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF150400000000DF160100DF170100DF14039F3704DF1801319F7B06000000010000DF1906000000010000DF2006000000050000DF2106000000004000");
        aid.add("9F0607A0000005241011DF0101009F08020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000");
*/      aid.add("9F0607A0000000031010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000032010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000033010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000034010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000035010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000038010DF0101009F0802008C9F0902008CDF1105D84000A800DF1205D84004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        //Amex
        aid.add("9F0606A00000002501DF0101009F080200019F09020001DF1105DC50840000DF1205C400000000DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801C89F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        //Master
        aid.add("9F0607A0000000041010DF0101009F080200029F09020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000043060DF0101009F080200029F09020002DF1105FC50ACA000DF1205F850ACF800DF130500008000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A0000000046000DF0101009F080200029F09020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        //Rupay
        aid.add("9F0607A00000052410109F080200649F09020064DF010100DF1105FFFFFFFFFFDF1205FFFFFFFFFFDF130500100000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");
        aid.add("9F0607A00000015230109F080200019F09020001DF010100DF1105FFFFFFFFFFDF1205FFFFFFFFFFDF130500100000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000999999999DF1906000000000000DF2006000999999999DF2106000000500000");


        return deviceHelper.downLoadAid(aid);
    }

    private String loadCAPKs(){
        ArrayList<String> capks = new ArrayList<>();
        /*capks.add("9F0605A0000003339F220102DF050420211231DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060");
        capks.add("9F0605A0000003339F220103DF050420221231DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26");
        capks.add("9F0605A0000003339F220104DF050420221231DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5");
        */

        capks.add("9F0605A0000005249F22016DDF050420241231DF060101DF070101DF0281F8B747E8CB3615E8D26231355488F3C76C4746F7BB1C381E6C6E6ABF0A6D7CD93CFC6B2C310288CA8BE7EE1730DE621A59D1BB2D8C02C9148FA06E5D1F5E672EEFCE8AECBAD4A1C18F3175F1BEA1AEF539376592366B46A5044E32E59B3F35F50E85F843BA01851E5386B7EBE27367D3D483C5472D3020AF42116DDDA32341557EBABB043EBC6006B99A652009045BFA50C527028586E05942E1D594223B49FE8566931C31FBE8C903ABD4F283E1FAB03D758247EC4B728A85A9897601B753293263ADBD10BE988D0C52FE0091C2721DC02C5130FC7663E95739A70EE2F84DFD2E50C88A1A26587EF7CC047FCA2D03C2CF0CE4B524B4EC3F07DF0314411008F9921B89C62E2160F6D0358614115ECD4ADF040103");
        capks.add("9F0605A0000000039F220101DF050420091231DF060101DF070101DF028080C696034213D7D8546984579D1D0F0EA519CFF8DEFFC429354CF3A871A6F7183F1228DA5C7470C055387100CB935A712C4E2864DF5D64BA93FE7E63E71F25B1E5F5298575EBE1C63AA617706917911DC2A75AC28B251C7EF40F2365912490B939BCA2124A30A28F54402C34AECA331AB67E1E79B285DD5771B5D9FF79EA630B75DF0314D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CCDF040103");
        capks.add("9F0605A0000000039F220107DF050420121231DF060101DF070101DF029090A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725FDF0314B4BC56CC4E88324932CBC643D6898F6FE593B172DF040103");
        capks.add("9F0605A0000000039F220108DF050420141231DF060101DF070101DF0280B0D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0BDF031420D213126955DE205ADC2FD2822BD22DE21CF9A8DF040103");
        capks.add("9F0605A0000000039F220109DF050420161231DF060101DF070101DF0281F89D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41DF03141FF80A40173F52D7D27E0F26A146A1C8CCB29046DF040103");
        capks.add("9F0605A0000000039F220157DF050420151231DF060101DF070101DF028060942B7F2BA5EA307312B63DF77C5243618ACC2002BD7ECB74D821FE7BDC78BF28F49F74190AD9B23B9713B140FFEC1FB429D93F56BDC7ADE4AC075D75532C1E590B21874C7952F29B8C0F0C1CE3AEEDC8DA25343123E71DCF86C6998E15F756E3DF0314251A5F5DE61CF28B5C6E2B5807C0644A01D46FF5DF0403010001");
        capks.add("9F0605A0000000039F220190DF050420151231DF060101DF070101DF028040C26B3CB3833E42D8270DC10C8999B2DA18106838650DA0DBF154EFD51100AD144741B2A87D6881F8630E3348DEA3F78038E9B21A697EB2A6716D32CBF26086F1DF0314B3AE2BC3CAFC05EEEFAA46A2A47ED51DE679F823DF040103");
        capks.add("9F0605A0000000039F220192DF050420151231DF060101DF070101DF0280B0996AF56F569187D09293C14810450ED8EE3357397B18A2458EFAA92DA3B6DF6514EC060195318FD43BE9B8F0CC669E3F844057CBDDF8BDA191BB64473BC8DC9A730DB8F6B4EDE3924186FFD9B8C7735789C23A36BA0B8AF65372EB57EA5D89E7D14E9C7B6B557460F10885DA16AC923F15AF3758F0F03EBD3C5C2C949CBA306DB44E6A2C076C5F67E281D7EF56785DC4D75945E491F01918800A9E2DC66F60080566CE0DAF8D17EAD46AD8E30A247C9FDF0314429C954A3859CEF91295F663C963E582ED6EB253DF040103");
        capks.add("9F0605A0000000039F220194DF050420151231DF060101DF070101DF0281F8ACD2B12302EE644F3F835ABD1FC7A6F62CCE48FFEC622AA8EF062BEF6FB8BA8BC68BBF6AB5870EED579BC3973E121303D34841A796D6DCBC41DBF9E52C4609795C0CCF7EE86FA1D5CB041071ED2C51D2202F63F1156C58A92D38BC60BDF424E1776E2BC9648078A03B36FB554375FC53D57C73F5160EA59F3AFC5398EC7B67758D65C9BFF7828B6B82D4BE124A416AB7301914311EA462C19F771F31B3B57336000DFF732D3B83DE07052D730354D297BEC72871DCCF0E193F171ABA27EE464C6A97690943D59BDABB2A27EB71CEEBDAFA1176046478FD62FEC452D5CA393296530AA3F41927ADFE434A2DF2AE3054F8840657A26E0FC617DF0314C4A3C43CCF87327D136B804160E47D43B60E6E0FDF040103");
        capks.add("9F0605A0000000039F220195DF050420151231DF060101DF070101DF028090BE9E1FA5E9A803852999C4AB432DB28600DCD9DAB76DFAAA47355A0FE37B1508AC6BF38860D3C6C2E5B12A3CAAF2A7005A7241EBAA7771112C74CF9A0634652FBCA0E5980C54A64761EA101A114E0F0B5572ADD57D010B7C9C887E104CA4EE1272DA66D997B9A90B5A6D624AB6C57E73C8F919000EB5F684898EF8C3DBEFB330C62660BED88EA78E909AFF05F6DA627BDF0314EE1511CEC71020A9B90443B37B1D5F6E703030F6DF040103");
        capks.add("9F0605A0000000039F220199DF050420151231DF060101DF070101DF028080AB79FCC9520896967E776E64444E5DCDD6E13611874F3985722520425295EEA4BD0C2781DE7F31CD3D041F565F747306EED62954B17EDABA3A6C5B85A1DE1BEB9A34141AF38FCF8279C9DEA0D5A6710D08DB4124F041945587E20359BAB47B7575AD94262D4B25F264AF33DEDCF28E09615E937DE32EDC03C54445FE7E382777DF03144ABFFD6B1C51212D05552E431C5B17007D2F5E6DDF040103");
        capks.add("9F0605A0000000049F220103DF050420091231DF060101DF070101DF028080C2490747FE17EB0584C88D47B1602704150ADC88C5B998BD59CE043EDEBF0FFEE3093AC7956AD3B6AD4554C6DE19A178D6DA295BE15D5220645E3C8131666FA4BE5B84FE131EA44B039307638B9E74A8C42564F892A64DF1CB15712B736E3374F1BBB6819371602D8970E97B900793C7C2A89A4A1649A59BE680574DD0B60145DF03145ADDF21D09278661141179CBEFF272EA384B13BBDF040103");
        capks.add("9F0605A0000000049F220104DF050420121231DF060101DF070101DF028090A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5DF0314381A035DA58B482EE2AF75F4C3F2CA469BA4AA6CDF040103");
        capks.add("9F0605A0000000049F220105DF050420141231DF060101DF070101DF0280B0B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597DF0314EBFA0D5D06D8CE702DA3EAE890701D45E274C845DF040103");
        capks.add("9F0605A0000000049F220106DF050420161231DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9DF040103");
        capks.add("9F0605A0000000049F2201EFDF050420151231DF060101DF070101DF0281F8A191CB87473F29349B5D60A88B3EAEE0973AA6F1A082F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A13ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB651AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3BDF031421766EBB0EE122AFB65D7845B73DB46BAB65427ADF040103");
        capks.add("9F0605A0000000049F2201F0DF050420151231DF060101DF070101DF0280D0999EA2D430D60614E100706C7DA213E1C77AD18C11BD70BC42CEBD80A3C94EC5E736D345EA7ADE2B9E0BC8816E567D39412EB728C2B2CCE73DEBC9FA25D4919BF5420C986083FBC0750895AFBA6B9DAA62B1B7D8439CF29E720D085D5D0962A9443B1F738E6560EF0EED7572815EA87A1B07570F119867DD6CC5D4DE06AA5373847D17A610ECF932FA2C94234E68AF84A9E0DAA18116B326016B70136F493482FEAE98E4AE682BF96C59279752248DEC915ED6F9BB73F9206155D961B50865E1CA6D47322FCE22DCF1957182B6E99CBBDF0314B8EA49169B54F3B7FF0DF3A8B6388C82A1DBE730DF040103");
        capks.add("9F0605A0000000049F2201F1DF050420151231DF060101DF070101DF0280B0A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7DF0314D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BBDF040103");
        capks.add("9F0605A0000000049F2201F3DF050420091231DF060101DF070101DF02809098F0C770F23864C2E766DF02D1E833DFF4FFE92D696E1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA0124723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A350C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3DF0314A69AC7603DAF566E972DEDC2CB433E07E8B01A9ADF040103");
        capks.add("9F0605A0000000049F2201F8DF050420151231DF060101DF070101DF028080A1F5E1C9BD8650BD43AB6EE56B891EF7459C0A24FA84F9127D1A6C79D4930F6DB1852E2510F18B61CD354DB83A356BD190B88AB8DF04284D02A4204A7B6CB7C5551977A9B36379CA3DE1A08E69F301C95CC1C20506959275F41723DD5D2925290579E5A95B0DF6323FC8E9273D6F849198C4996209166D9BFC973C361CC826E1DF0314F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DDDF040103");
        capks.add("9F0605A0000000049F2201FADF050420151231DF060101DF070101DF028090A90FCD55AA2D5D9963E35ED0F440177699832F49C6BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDAB5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E7013536C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B108867DEC40FAAECD740C00E2B7A8852DDF03145BED4068D96EA16D2D77E03D6036FC7A160EA99CDF040103");
        capks.add("9F0605A0000000049F2201FEDF050420091231DF060101DF070101DF028080A653EAC1C0F786C8724F737F172997D63D1C3251C44402049B865BAE877D0F398CBFBE8A6035E24AFA086BEFDE9351E54B95708EE672F0968BCD50DCE40F783322B2ABA04EF137EF18ABF03C7DBC5813AEAEF3AA7797BA15DF7D5BA1CBAF7FD520B5A482D8D3FEE105077871113E23A49AF3926554A70FE10ED728CF793B62A1DF03149A295B05FB390EF7923F57618A9FDA2941FC34E0DF040103");
        capks.add("9F0605A0000001529F220101DF050420151231DF060101DF070101DF0280808D1727AB9DC852453193EA0810B110F2A3FD304BE258338AC2650FA2A040FA10301EA53DF18FD9F40F55C44FE0EE7C7223BC649B8F9328925707776CB86F3AC37D1B22300D0083B49350E09ABB4B62A96363B01E4180E158EADDD6878E85A6C9D56509BF68F0400AFFBC441DDCCDAF9163C4AACEB2C3E1EC13699D23CDA9D3ADDF0314E0C2C1EA411DB24EC3E76A9403F0B7B6F406F398DF040103");
        capks.add("9F0605A0000001529F22015ADF050420151231DF060101DF070101DF028080EDD8252468A705614B4D07DE3211B30031AEDB6D33A4315F2CFF7C97DB918993C2DC02E79E2FF8A2683D5BBD0F614BC9AB360A448283EF8B9CF6731D71D6BE939B7C5D0B0452D660CF24C21C47CAC8E26948C8EED8E3D00C016828D642816E658DC2CFC61E7E7D7740633BEFE34107C1FB55DEA7FAAEA2B25E85BED948893D07DF031495F4D045422D0920D04E9614B714D936DEA1AACADF040103");
        capks.add("9F0605A0000001529F22015BDF050420151231DF060101DF070101DF028090D3F45D065D4D900F68B2129AFA38F549AB9AE4619E5545814E468F382049A0B9776620DA60D62537F0705A2C926DBEAD4CA7CB43F0F0DD809584E9F7EFBDA3778747BC9E25C5606526FAB5E491646D4DD28278691C25956C8FED5E452F2442E25EDC6B0C1AA4B2E9EC4AD9B25A1B836295B823EDDC5EB6E1E0A3F41B28DB8C3B7E3E9B5979CD7E079EF024095A1D19DDDF03144DC5C6CAB6AE96974D9DC8B2435E21F526BC7A60DF040103");
        capks.add("9F0605A0000001529F22015CDF050420151231DF060101DF070101DF0280B0833F275FCF5CA4CB6F1BF880E54DCFEB721A316692CAFEB28B698CAECAFA2B2D2AD8517B1EFB59DDEFC39F9C3B33DDEE40E7A63C03E90A4DD261BC0F28B42EA6E7A1F307178E2D63FA1649155C3A5F926B4C7D7C258BCA98EF90C7F4117C205E8E32C45D10E3D494059D2F2933891B979CE4A831B301B0550CDAE9B67064B31D8B481B85A5B046BE8FFA7BDB58DC0D7032525297F26FF619AF7F15BCEC0C92BCDCBC4FB207D115AA65CD04C1CF982191DF031460154098CBBA350F5F486CA31083D1FC474E31F8DF040103");
        // added on 3/2/2026
        capks.add("9F0607A00000000410109F2201EFDF050420301231DF060101DF070101DF0281F8A191CB87473F29349B5D60A88B3EAEE0973AA6F1A082F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A13ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB651AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3BDF031421766EBB0EE122AFB65D7845B73DB46BAB65427ADF040103");
        return deviceHelper.downLoadCapk(capks);
    }

    private boolean loadEMV(){
        //String emvParam = "9F01063132333435369F40057000F0A0019F150230319F160F3132333435363738393031323334359F3901059F330360D0C89F1A0203569F1C0831323334353637389F3501225F2A0203565F3601029F3C0203569F3D01029F1E086D665F36306220209F660434000080";
          String emvParam = "9F01063132333435369F4005E000F0A0019F150230319F160F3132333435363738393031323334359F3901059F330360D0C89F1A0203569F1C0831323334353637389F3501225F2A0203565F3601029F3C0203569F3D01029F1E086D665F36306220209F660434000080";
        return deviceHelper.setEmvParam(emvParam);
    }

    private String preparePosDataCode(String txnFunctionCode, String agentMobile, String agentPinCode) {
        StringBuilder posDataCode = new StringBuilder();

        // Subfield 1: Card Data Input Capability
        // 4 - Magnetic stripe and ICC capability
        posDataCode.append(4);

        // Subfield 2: Cardholder Authentication Capability
        // 2 - PIN Entry
        posDataCode.append(2);

        // Subfield 3: Card Capture Capability
        // 0 - Unknown
        // 1 - No capture capability
        posDataCode.append(1);

        // Subfield 4: Terminal Operating Environment
        // 1 - On premises of card acceptor, attended
        posDataCode.append(1);

        // Subfield 5: Cardholder Present Data
        // 1 - Cardholder present
        posDataCode.append(1);

        // Subfield 6: Card Present Data
        // 2 - Card Present
        posDataCode.append(2);

        // Subfield 7: Card Data Input Mode
        // 2 - Magnetic Stripe read
        // 3 - Online chip
        if ("MCR".equals(txnFunctionCode) || "FALLBACK".equals(txnFunctionCode)) {
            posDataCode.append(2);
        } else {
            posDataCode.append(3);
        }

        // Subfield 8: Cardholder Authentication method
        // 2 - PIN
        posDataCode.append(2);

        // Subfield 9: Cardholder Authentication Entity
        // 0 - Unknown for magstripe
        // 1 - ICC
        if ("MCR".equals(txnFunctionCode) || "FALLBACK".equals(txnFunctionCode)) {
            posDataCode.append(0);
        } else {
            posDataCode.append(1);
        }

        // Subfield 10: Card Data Output Capability
        // 0 - Unknown
        posDataCode.append(0);

        // Subfield 11: Terminal Data Output Capability
        // 3 - Print and Display Capability
        posDataCode.append(3);

        // Subfield 12: PIN Capture capability
        // 3 - 6 chars maximum
        posDataCode.append(3);

        // Subfield 13 position 13-21: Zip Code
        // 000400013 - Merchant Postal Code n
        posDataCode.append(padLeftString(agentPinCode, 9, '0'));

        // Subfield 14 position 22-41:POS Additional Merchant Address Data
        posDataCode.append(padRightString(agentMobile, 20, ' '));

        return posDataCode.toString();
    }

    private String padLeftString(String v, int padCount, char paddingChar) {
        return String.format("%1$" + padCount + "s", v).replace(' ', paddingChar);
    }

    private String padRightString(String v, int padCount, char paddingChar) {
        return String.format("%1$-" + padCount + "s", v).replace(' ', paddingChar);
    }

    @Override
    public PublickeyResponse getPublicKey() {
        PublickeyResponse keyResponse = new PublickeyResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        try {
            KeyPair keyPair = rsaUtil.generateRSAKkeyPair();
            keyResponse.setPublicKey(new String(Base64.getEncoder().encode(rsaUtil.getPublicKey().getEncoded())));
            responseStatus.setCode("00");
            responseStatus.setStatus("Success");
            responseStatus.setMessage("Success");
            keyResponse.setResponseStatus(responseStatus);
        } catch (Exception e) {
            log.error("error on key pair generation :",e);
            responseStatus.setCode("151");
            responseStatus.setStatus("Failed");
            responseStatus.setMessage("Error on Key pair generation");
            keyResponse.setResponseStatus(responseStatus);
        }
        return keyResponse;
    }

    @Override
    public PosSetUpResponse setPosSetup(PosSetupRequest posSetupRequest) {
        PosSetUpResponse response = new PosSetUpResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        int defaultVendorId = 28;
        try {
                if(StringUtils.isNotBlank(posSetupRequest.getVendorId())) {
                    defaultVendorId = Integer.parseInt(posSetupRequest.getVendorId());
                }
                String vendorResp = services.setVendorIdService(defaultVendorId);
                if (!"success".equalsIgnoreCase(vendorResp)) {
                    return buildFailureResponse(response, responseStatus,
                            "Device connect fail - setVendor api failed");
                }

                //Set Connection Mode
                String connectionModeResp = services.connectionModeService(0, "HID");
                if (!"success".equalsIgnoreCase(connectionModeResp)) {
                    return buildFailureResponse(response, responseStatus,
                            "Device connect fail - setConnectionMode api failed");
                }

                //Connect USB
                boolean connectUsb = deviceHelper.connect(posSetupRequest.getDeviceAddress());
                if (!connectUsb) {
                    notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
                    return buildFailureResponse(response, responseStatus,
                            "Device connect fail");
                }
                // Load AIDs
                if (isFailure(loadAids(), "Download aid fail")) {
                    return buildFailureResponse(response, responseStatus, "AID download failed");
                }

                // Load CAPKs
                if (isFailure(loadCAPKs(), "Download CAPK ERROR")) {
                    return buildFailureResponse(response, responseStatus, "CAPK download failed");
                }

                //deviceHelper.setBitmapImages();

                // Success
                responseStatus.setCode("00");
                responseStatus.setStatus("Success");
                responseStatus.setMessage("Success");
                response.setResponseStatus(responseStatus);

        } catch (Exception e) {
            log.error("Error during POS setup", e);
            return buildFailureResponse(response, responseStatus, "Unexpected error occurred");
        }

        return response;
    }

    private PosSetUpResponse isDeviceConnected(PosSetupRequest request, PosSetUpResponse response, ResponseStatus status) {
        if (deviceHelper.isConnected()) {
            return null; // Already connected
        }

        if (!"success".equalsIgnoreCase(services.setVendorIdService(28))) {
            return buildFailureResponse(response, status, "Device connect fail - setVendor API failed");
        }

        if (!"success".equalsIgnoreCase(services.connectionModeService(0, "HID"))) {
            return buildFailureResponse(response, status, "Device connect fail - setConnectionMode API failed");
        }

        if (!deviceHelper.connect(request.getDeviceAddress())) {
            notificationService.showCustomNotification(null, MF_DEVICE_NOT_DETECTED_PLEASE_CHECK_USB_PORT);
            return buildFailureResponse(response, status, "Device connect fail");
        }
        return null; // Connected successfully
    }

    private boolean isFailure(String result, String failureMessage) {
        return result == null || result.equalsIgnoreCase(failureMessage);
    }

    public void setBitmap(TipListener listener) {
        final Display display = Display.getDefault();
        Image image = new Image(display, "images/welcome.png");

        listener.onTip("Loading welcome");
        Controler.getInstance().setBitmap(EnumBitmapLocation.ROM, 1, 0, 0, 128, 16, image);

        listener.onTip("Loading searchcard");
        image = new Image(display, "images/searchcard.png");
        Controler.getInstance().setBitmap(EnumBitmapLocation.RAM, 1, 0, 0, 128, 48, image);

        listener.onTip("Loading reading");
        image = new Image(display, "images/reading.png");
        Controler.getInstance().setBitmap(EnumBitmapLocation.ROM, 2, 0, 0, 128, 48, image);

        listener.onTip("Loading cardreadfail");
        image = new Image(display, "images/cardreadfail.png");
        Controler.getInstance().setBitmap(EnumBitmapLocation.ROM, 3, 0, 0, 128, 48, image);

        listener.onTip("Loading inputpin");
        image = new Image(display, "images/inputpin.png");
        Controler.getInstance().setBitmap(EnumBitmapLocation.RAM, 2, 0, 0, 128, 48, image);

        listener.onTip("Set bitmap success");

    }

}
