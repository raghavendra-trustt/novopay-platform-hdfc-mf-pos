package com.in.novopay.hdfc.mf.pos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import com.in.novopay.hdfc.mf.pos.notifications.NotificationService;
import com.morefun.mpos.sdk.constants.*;
import com.morefun.mpos.sdk.result.*;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.morefun.mpos.sdk.Controler;
import com.morefun.mpos.sdk.emv.EmvTagDef;
import com.morefun.mpos.sdk.listener.IUpdatePosProc;
import com.morefun.mpos.sdk.param.ReadCardParam;
import com.morefun.mpos.sdk.utils.BytesUtils;


import utils.BytesUtil;
import utils.RSAUtil;

@Component
@Slf4j
public class DeviceHelper {
	public static DeviceHelper instance;

	@Autowired
	NotificationService notificationService;

	/*public DeviceHelper() {
		
	}
	
	public static DeviceHelper getInstance() {
		if (instance == null) {
			instance = new DeviceHelper();
		}
		return instance;
	}*/
	
	public int init(EnumConnectMode mode, int id) {
		 Controler.getInstance().init(mode, id);
		 return 0;
	}
	
	public boolean connect(String address) {
		ConnectPosResult ret = Controler.getInstance().connectPos(address);
		return ret.bConnected;
	}
	
	public boolean isConnected()
	{
		return  Controler.getInstance().posConnected();
	}
	public boolean disconnect() {
		Controler.getInstance().disconnect();
		return true;
	}
	
	public ReadPosInfoResult readPosInfo() {//need to implement
		ReadPosInfoResult result = Controler.getInstance().readPosInfo();
//        StringBuilder builder = new StringBuilder();
//        builder.append("\nposVer:" + result.posVer);
//        builder.append("\ndataVer:" + result.dataVer);
//        builder.append("\nmodel:" + result.model);
//        builder.append("\nsn:" + result.sn);
//        builder.append("\nStatus:" + result.initStatus);
        return result;
	}
	
	public  ReadCardResult readCard(ReadCardModel readCardModel) throws IllegalArgumentException, IllegalAccessException {
        ReadCardParam param = new ReadCardParam();
        param.setAllowfallback(readCardModel.isAllowfallback());
        param.setAmount(Long.parseLong(readCardModel.getAmount()));
        param.setPinInput((byte) readCardModel.getPinInput());
        param.setPinMaxLen(readCardModel.getPinMaxLen());
        param.setCardTimeout(readCardModel.getCardTimeout());
        param.setMposFourthLineContent("FourthLine Content");
        
        param.setRequireReturnCardNo(readCardModel.getRequiretype());
        List<byte[]> tags = new ArrayList<>();
   
        
        tags.add(EmvTagDef.EMV_TAG_9F02_TM_AUTHAMNTN);
        tags.add(EmvTagDef.EMV_TAG_9F26_IC_AC);
        tags.add(EmvTagDef.EMV_TAG_9F27_IC_CID);
        tags.add(EmvTagDef.EMV_TAG_9F10_IC_ISSAPPDATA);
        tags.add(EmvTagDef.EMV_TAG_9F37_TM_UNPNUM);
        tags.add(EmvTagDef.EMV_TAG_9F36_IC_ATC);
        tags.add(EmvTagDef.EMV_TAG_95_TM_TVR);
        tags.add(EmvTagDef.EMV_TAG_9A_TM_TRANSDATE);
        tags.add(EmvTagDef.EMV_TAG_9C_TM_TRANSTYPE);
        tags.add(EmvTagDef.EMV_TAG_5F2A_TM_CURCODE);
        tags.add(EmvTagDef.EMV_TAG_82_IC_AIP);
        tags.add(EmvTagDef.EMV_TAG_9F1A_TM_CNTRYCODE);
        tags.add(EmvTagDef.EMV_TAG_9F03_TM_OTHERAMNTN);
        tags.add(EmvTagDef.EMV_TAG_9F33_TM_CAP);
        tags.add(EmvTagDef.EMV_TAG_9F34_TM_CVMRESULT);
        tags.add(EmvTagDef.EMV_TAG_9F35_TM_TERMTYPE);
        tags.add(EmvTagDef.EMV_TAG_9F1E_TM_IFDSN);
        tags.add(EmvTagDef.EMV_TAG_84_IC_DFNAME);
        tags.add(EmvTagDef.EMV_TAG_9F09_TM_APPVERNO);
        tags.add(EmvTagDef.EMV_TAG_9F63_TM_BIN);
        tags.add(EmvTagDef.EMV_TAG_9F41_TM_TRSEQCNTR);
        tags.add(EmvTagDef.EMV_TAG_9F12_IC_APNAME);
        tags.add(EmvTagDef.EMV_TAG_50_IC_APPLABEL);
        tags.add(EmvTagDef.EMV_TAG_57_IC_TRACK2EQUDATA);
		tags.add(EmvTagDef.EMV_TAG_5F34_IC_PANSN);
		tags.add(EmvTagDef.EMV_TAG_5F28_IC_ISSCOUNTRYCODE);
		tags.add(EmvTagDef.EMV_TAG_9B_TM_TSI);
		tags.add(EmvTagDef.EMV_TAG_9F06_TM_AID);
		tags.add(EmvTagDef.EMV_TAG_9F07_IC_AUC);
		tags.add(EmvTagDef.EMV_TAG_9F08_IC_APPVERNO);
		tags.add(EmvTagDef.EMV_TAG_5F30_IC_SERVICECODE);
		tags.add(EmvTagDef.EMV_TAG_9F01_TM_ACQID);
		tags.add(EmvTagDef.EMV_TAG_4F_IC_AID);
		tags.add(EmvTagDef.EMV_TAG_9F21_TM_TRANSTIME);

		param.setTags(tags);
        
        
    switch (readCardModel.getEmvTransactionType()) {
		case TransType.FUNC_SALE:
			 param.setTransType(EnumTransType.FUNC_SALE);
			break;
		case TransType.FUNC_BALANCE:
			 param.setTransType(EnumTransType.FUNC_BALANCE);
			break;
		case TransType.FUNC_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_PREAUTH);
			break;
		case TransType.FUNC_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_AUTHSALE);
			break;
		case TransType.FUNC_AUTHSALEOFF:
			 param.setTransType(EnumTransType.FUNC_AUTHSALEOFF);
			break;
		case TransType.FUNC_AUTHSETTLE:
			 param.setTransType(EnumTransType.FUNC_AUTHSETTLE);
			break;
		case TransType.FUNC_ADDTO_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_ADDTO_PREAUTH);
			break;
		case TransType.FUNC_REFUND:
			 param.setTransType(EnumTransType.FUNC_REFUND);
			break;
		case TransType.FUNC_VOID_SALE:
			 param.setTransType(EnumTransType.FUNC_VOID_SALE);
			break;
		case TransType.FUNC_VOID_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_VOID_AUTHSALE);
			break;
		case TransType.FUNC_VOID_AUTHSETTLE:
			 param.setTransType(EnumTransType.FUNC_VOID_AUTHSETTLE);
			break;
		case TransType.FUNC_VOID_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_VOID_PREAUTH);
			break;
		case TransType.FUNC_VOID_REFUND:
			 param.setTransType(EnumTransType.FUNC_VOID_REFUND);
			break;			
		case TransType.FUNC_OFFLINE:
			 param.setTransType(EnumTransType.FUNC_OFFLINE);
			break;
		case TransType.FUNC_ADJUST:
			 param.setTransType(EnumTransType.FUNC_ADJUST);
			break;
		case TransType.FUNC_EP_LOAD:
			 param.setTransType(EnumTransType.FUNC_EP_LOAD);
			break;
		case TransType.FUNC_EP_PURCHASE:
			 param.setTransType(EnumTransType.FUNC_EP_PURCHASE);
			break;
		case TransType.FUNC_CASH_EP_LOAD:
			 param.setTransType(EnumTransType.FUNC_CASH_EP_LOAD);
			break;
		case TransType.FUNC_NOT_BIND_EP_LOAD:
			 param.setTransType(EnumTransType.FUNC_NOT_BIND_EP_LOAD);
			break;
		case TransType.FUNC_INSTALMENT:
			 param.setTransType(EnumTransType.FUNC_INSTALMENT);
			break;
		case TransType.FUNC_VOID_INSTALMENT:
			 param.setTransType(EnumTransType.FUNC_VOID_INSTALMENT);
			break;
		case TransType.FUNC_BONUS_IIS_SALE:
			 param.setTransType(EnumTransType.FUNC_BONUS_IIS_SALE);
			break;
		case TransType.FUNC_VOID_BONUS_IIS_SALE:
			 param.setTransType(EnumTransType.FUNC_VOID_BONUS_IIS_SALE);
			break;
		case TransType.FUNC_BONUS_ALLIANCE:
			 param.setTransType(EnumTransType.FUNC_BONUS_ALLIANCE);
			break;
		case TransType.FUNC_VOID_BONUS_ALLIANCE:
			 param.setTransType(EnumTransType.FUNC_VOID_BONUS_ALLIANCE);
			break;
		case TransType.FUNC_ALLIANCE_BALANCE:
			 param.setTransType(EnumTransType.FUNC_ALLIANCE_BALANCE);
			break;

		case TransType.FUNC_ALLIANCE_REFUND:
			 param.setTransType(EnumTransType.FUNC_ALLIANCE_REFUND);
			break;
		case TransType.FUNC_INTEGRALSIGNIN:
			 param.setTransType(EnumTransType.FUNC_INTEGRALSIGNIN);
			break;
		case TransType.FUNC_QPBOC:
			 param.setTransType(EnumTransType.FUNC_QPBOC);
			break;
		case TransType.FUNC_EC_PURCHASE:
			 param.setTransType(EnumTransType.FUNC_EC_PURCHASE);
			break;
		case TransType.FUNC_EC_LOAD:
			 param.setTransType(EnumTransType.FUNC_EC_LOAD);
			break;
		case TransType.FUNC_EC_LOAD_CASH:
			 param.setTransType(EnumTransType.FUNC_EC_LOAD_CASH);
			break;
		case TransType.FUNC_EC_NOT_BIND_OUT:
			 param.setTransType(EnumTransType.FUNC_EC_NOT_BIND_OUT);
			break;
		case TransType.FUNC_EC_NOT_BIND_IN:
			 param.setTransType(EnumTransType.FUNC_EC_NOT_BIND_IN);
			break;
		case TransType.FUNC_EC_VOID_LOAD_CASH:
			 param.setTransType(EnumTransType.FUNC_EC_VOID_LOAD_CASH);
			break;
		case TransType.FUNC_EC_REFUND:
			 param.setTransType(EnumTransType.FUNC_EC_REFUND);
			break;
		case TransType.FUNC_EC_BALANCE:
			 param.setTransType(EnumTransType.FUNC_EC_BALANCE);
			break;
		case TransType.FUNC_APPOINTMENT_SALE:
			 param.setTransType(EnumTransType.FUNC_APPOINTMENT_SALE);
			break;
		case TransType.FUNC_VOID_APPOINTMENT_SALE:
			 param.setTransType(EnumTransType.FUNC_VOID_APPOINTMENT_SALE);
			break;			
		case TransType.FUNC_MAG_LOAD_CASH:
			 param.setTransType(EnumTransType.FUNC_MAG_LOAD_CASH);
			break;
		case TransType.FUNC_MAG_LOAD_ACCOUNT:
			 param.setTransType(EnumTransType.FUNC_MAG_LOAD_ACCOUNT);
			break;
		case TransType.FUNC_PHONE_SALE:
			 param.setTransType(EnumTransType.FUNC_PHONE_SALE);
			break;
		case TransType.FUNC_VOID_PHONE_SALE:
			 param.setTransType(EnumTransType.FUNC_VOID_PHONE_SALE);
			break;
		case TransType.FUNC_REFUND_PHONE_SALE:
			 param.setTransType(EnumTransType.FUNC_REFUND_PHONE_SALE);
			break;
		case TransType.FUNC_PHONE_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_PHONE_PREAUTH);
			break;
		case TransType.FUNC_VOID_PHONE_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_VOID_PHONE_PREAUTH);
			break;
		case TransType.FUNC_PHONE_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_PHONE_AUTHSALE);
			break;
		case TransType.FUNC_PHONE_AUTHSALEOFF:
			 param.setTransType(EnumTransType.FUNC_PHONE_AUTHSALEOFF);
			break;
		case TransType.FUNC_VOID_PHONE_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_VOID_PHONE_AUTHSALE);
			break;
		case TransType.FUNC_PHONE_BALANCE:
			 param.setTransType(EnumTransType.FUNC_PHONE_BALANCE);
			break;
		case TransType.FUNC_ORDER_SALE:
			 param.setTransType(EnumTransType.FUNC_ORDER_SALE);
			break;
		case TransType.FUNC_VOID_ORDER_SALE:
			 param.setTransType(EnumTransType.FUNC_ALLIANCE_BALANCE);
			break;
		case TransType.FUNC_ORDER_PREAUTH:
			 param.setTransType(EnumTransType.FUNC_ORDER_PREAUTH);
			break;
		case TransType.FUNC_ORDER_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_ORDER_AUTHSALE);
			break;
		case TransType.FUNC_VOID_ORDER_AUTHSALE:
			 param.setTransType(EnumTransType.FUNC_VOID_ORDER_AUTHSALE);
			break;			
		case TransType.FUNC_ORDER_AUTHSALEOFF:
			 param.setTransType(EnumTransType.FUNC_ORDER_AUTHSALEOFF);
			break;
		case TransType.FUNC_ORDER_REFUND:
			 param.setTransType(EnumTransType.FUNC_ORDER_REFUND);
			break;
		case TransType.FUNC_EMV_SCRIPE:
			 param.setTransType(EnumTransType.FUNC_EMV_SCRIPE);
			break;
		case TransType.FUNC_EMV_REFUND:
			 param.setTransType(EnumTransType.FUNC_EMV_REFUND);
			break;
		case TransType.FUNC_PBOC_LOG:
			 param.setTransType(EnumTransType.FUNC_PBOC_LOG);
			break;
		case TransType.FUNC_LOAD_LOG:
			 param.setTransType(EnumTransType.FUNC_LOAD_LOG);
			break;
		case TransType.FUNC_REVERSAL:
			 param.setTransType(EnumTransType.FUNC_REVERSAL);
			break;
		case TransType.FUNC_TC:
			 param.setTransType(EnumTransType.FUNC_TC);
			break;
		case TransType.FUNC_SETTLE:
			 param.setTransType(EnumTransType.FUNC_SETTLE);
			break;
		case TransType.COUNTTRANSTYPECOUNT:
			 param.setTransType(EnumTransType.COUNTTRANSTYPECOUNT);
			break;
		default:
			break;
		}
       
        
        param.setOnSteplistener(new ReadCardParam.onStepListener() {
            @Override
            public void onStep(byte step) {
                switch (step) {
                    case 1://waiting read card
                    	//state= "Waiting read card";
                    	com.morefun.mpos.sdk.Log.d("Waiting read card");
                        break;
                    case 2://Reading card
                    	com.morefun.mpos.sdk.Log.d("Waiting read card");
                        break;
                    case 3://Waiting enter the password
                    	com.morefun.mpos.sdk.Log.d("Waiting read card");
                        break;
                    case 4://Waiting enter the amount
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                }
            }
        });

        //set trans No.
        param.setLsh("000001");
		notificationService.showCustomNotification(null, "Please Insert/Swipe the card");
        ReadCardResult result=Controler.getInstance().ReadCard(param);

		return result;
        
	}
	
	
	 
	 
	  public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
	        try {
	            byte[] buffer = Base64.decodeBase64(privateKeyStr);
	            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
	            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	        } catch (NoSuchAlgorithmException e) {
	            throw new Exception("无此算法");
	        } catch (InvalidKeySpecException e) {
	            throw new Exception("私钥非法");
	        } catch (NullPointerException e) {
	            throw new Exception("私钥数据为空");
	        }
	    }
	public String downLoadAid(ArrayList<String> aids) {
        ICAidResult result = Controler.getInstance().aidAction(EnumAidAction.CLEAR, null);
        StringBuilder data=new StringBuilder();
        if (result.commResult.equals(EnumCommRet.NOERROR)) {

            for (int j = 0; j < aids.size(); j++) {
                String aid = aids.get(j);
                Controler.getInstance().aidAction(EnumAidAction.ADD, BytesUtils.asc2hex(aid));
                data.append( "Start download AID(" + (j + 1) + "/" + aids.size() + ")\n");
            }
            return data.toString();
			//return true;
        }
        return "Download aid fail";
		//return false;
	}
	
	public String downLoadCapk(ArrayList<String> capks) {
		CapkResult result = Controler.getInstance().capkAction(EnumCapkAction.CLEAR, null);
		StringBuilder data=new StringBuilder();
        if (result.commResult.equals(EnumCommRet.NOERROR)) {
            for (int j = 0; j < capks.size(); j++) {
                String rid = capks.get(j);
                Controler.getInstance().capkAction(EnumCapkAction.ADD, BytesUtils.asc2hex(rid));
                data.append("Downlaod Capk(" + (j + 1) + "/" + capks.size() + ")\n");
            }
            return data.toString();
			//return true;
        }else
        {
        	return "Download CAPK ERROR";
			//return false;
        }
	}
	public boolean setEmvParam(String emvParam)
	 {
		 return Controler.setEmvParamTlv(emvParam);
		 /*if(isLoaded)
		    return "success";
		 else 
			 return "failed";*/
		 
	 }
	public String  loadDukpt(DukptModel dukptModel) {
        byte[] bdk = BytesUtils.hexString2ByteArray(dukptModel.getBkd());//("C1D0F8FB4958670DBA40AB1F3752EF0D");
        byte[] ksn = BytesUtils.hexString2ByteArray(dukptModel.getKsn());//"FFFF9876543210000000");
        
        LoadDukptResult bret = Controler.getInstance().LoadDukpt((byte) 0x01, EnumKeyIndex.INDEX0, bdk, ksn);
        
        
       return "Load dukpt result:" + bret.loadResult + "\ncheckvalue:" + BytesUtils.hex2asc(bret.checkvalue);
	}
	
	public boolean loadKek(LoadKekModel loadKekModel) {
        byte[] key = BytesUtils.hexString2ByteArray(loadKekModel.getKey());//new String(BytesUtils.hexString2ByteArray("3438336362623738623465343262373865653832363539363630636637653539"));
        byte[] kvc = BytesUtils.hexString2ByteArray(loadKekModel.getKcv());//new String(BytesUtils.hexString2ByteArray("314539373132"));
        LoadKekResult bret = Controler.getInstance().loadKek(key, kvc);
        return bret.loadResult;
	}
	
	public boolean loadMasterKey(LoadMasterKeyModel loadMasterKeyModel) {
        String key = loadMasterKeyModel.getKey();//"DCA68732FA5C3D7C4FADE87D4C35F15A";
        String kvc = loadMasterKeyModel.getKcv();//"636B8475";
        LoadMainKeyResult result = Controler.getInstance().loadMainKey(EnumMainKeyEncType.KEK,
                EnumKeyIndex.INDEX0, 
                BytesUtils.hexString2ByteArray(key), 
                BytesUtils.hexString2ByteArray(kvc));

       return result.loadResult;
	}
	
	public boolean loadWorkKey(LoadWorkKeyModel loadWorkKeyModel) {
		/*LoadWorkKeyResult result=null;
       result=Controler.LoadWorkKey(EnumKeyIndex.INDEX0,EnumWorkKeyType.PIN,BytesUtils.hexString2ByteArray(loadWorkKeyModel.getPinKey()),BytesUtils.hexString2ByteArray(loadWorkKeyModel.getPinKCV()));
		 if(result.loadResult)
		 {
			
		    result= Controler.LoadWorkKey(EnumKeyIndex.INDEX0,EnumWorkKeyType.TDK,BytesUtils.hexString2ByteArray(loadWorkKeyModel.getTdkKey()),BytesUtils.hexString2ByteArray(loadWorkKeyModel.getTdkKcv()));
		    if(result.loadResult) return true;
				//return "keys loaded"+ true; else return "Key loading failed:"+ false;
		 }
		 //else return "Key loading failed:"+ false;*/

		/*byte[] pinKey = BytesUtils.hexString2ByteArray(loadWorkKeyModel.getPinKey());
		byte[] macKey = BytesUtils.hexString2ByteArray("B7C60530D82A361516E938B5343D2F7700000000");
		byte[] tdkKey = BytesUtils.hexString2ByteArray(loadWorkKeyModel.getTdkKey());
		LoadWorkKeyResult result = Controler.getInstance().loadWorkKey(EnumKeyIndex.INDEX0, pinKey, macKey, tdkKey);*/

		byte[] pinKey = BytesUtils.hexString2ByteArray(loadWorkKeyModel.getPinKey());
		byte[] pinKeyKcv = BytesUtils.hexString2ByteArray("00000000");
		//byte[] macKey = BytesUtils.hexString2ByteArray("97D4E799FB2ACE979C52867B64DC74B7");
		//byte[] macKeyKcv = BytesUtils.hexString2ByteArray("00000000");
		byte[] tdkKey = BytesUtils.hexString2ByteArray(loadWorkKeyModel.getTdkKey());
		byte[] tdkKeyKcv = BytesUtils.hexString2ByteArray("00000000");
		LoadWorkKeyResult pinkeyResult = Controler.getInstance().LoadWorkKey(EnumKeyIndex.INDEX0, EnumWorkKeyType.PIN, pinKey, pinKeyKcv);
		LoadWorkKeyResult tdkkeyResult = Controler.getInstance().LoadWorkKey(EnumKeyIndex.INDEX0, EnumWorkKeyType.TDK, tdkKey, tdkKeyKcv);
		return tdkkeyResult.loadResult && pinkeyResult.loadResult;

	}
	/*Implementation OF TR31*/
//	public String loadTR31TMK(LoadTR31TMK loadTR31TMK)
//	{
//		LoadTr31MainKeyResult result=null;
//		result=Controler.getInstance().loadTr31MainKey(BytesUtils.hexString2ByteArray(loadTR31TMK.getKey()));
//		if (result.loadResult)  return "kyes loaded"+ true; else return "Key loading failed:"+ false;
//	}
	public String loadTR31PinBlock(TR31PinBlock tr31PinBlock)
	{

		LoadTr31PinBlockResult result= Controler.getInstance().TR31KeyBlockManage(EnumTR31KeyBlokType.MAINKEY,EnumKeyIndex.INDEX0,
				BytesUtils.hexString2ByteArray(tr31PinBlock.getKey()) ,BytesUtils.hexString2ByteArray(tr31PinBlock.getKSN())
		);
	     if (result.loadResult) return "Key Loading..."+ true; else  return "Key Loading Failed"+ false;
	}
	public String loadTR31Kek(LoadTR31Kek loadTR31Kek){
		LoadTr31KekResult result = Controler.getInstance().loadTr31Kek(
				BytesUtils.hexString2ByteArray(loadTR31Kek.getKey()));
		if (result.loadResult)  return "kyes loaded"+ true; else return "Key loading failed:"+ false;

	}

	/*IMPLEMENTATION END OF TR31*/
	
	public String setTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String t = format.format(date);
        Controler.getInstance().setDateTime(t);
		return "Set time success";
		
		
	}
	
	public String  calcMac(CalculateMacModel calculateMacModel) {
		byte[] data = BytesUtils.hexString2ByteArray(calculateMacModel.getInputData());//"0102030405060708");
        StringBuilder builder = new StringBuilder();
		
		CalMacResult result = Controler.getInstance().calcMac(EnumMacAlg.ENCRYPTION_MAC_X919_DUCKPT, data, data.length);
		
		builder.append("CalcMac MacValue:" + BytesUtils.hex2asc(result.macValue));
		builder.append("\nCalcMac MacRandom:" + BytesUtils.hex2asc(result.macRandom));
		
		return  builder.toString();
		
	}
	
	public String onlineAuth1(String authData) {
        byte[] b = BytesUtils.asc2hex(authData);//9F360211ED910A80923B3E263AE39B3030
        List<byte[]> tags = new ArrayList<byte[]>();

        EmvDealOnlineRspResult r = Controler.getInstance().emvDealOnlineRsp(true, b, b.length, "");

        if (r.authResult== EnumEmvDealOnlineRsp.SUCC) {

            tags.add(new byte[]{(byte) 0x9F, (byte) 0x26});
            tags.add(new byte[]{(byte) 0x95});
            tags.add(new byte[]{(byte) 0x4F});
            tags.add(new byte[]{(byte) 0x5F, (byte) 0x34});
            tags.add(new byte[]{(byte) 0x9B});
            tags.add(new byte[]{(byte) 0x9F, (byte) 0x36});
            tags.add(new byte[]{(byte) 0x82});
            tags.add(new byte[]{(byte) 0x9F, (byte) 0x37});
            tags.add(new byte[]{(byte) 0x50});

            GetEmvDataResult rdata = Controler.getInstance().getEmvData(tags, false);
            if (rdata.commResult.equals(EnumCommRet.NOERROR)) {
            	return BytesUtils.hex2asc(rdata.tlvData);
            } else {
            	return r.commResult.toDisplayName();
            }
        } else {
        	return r.commResult.toDisplayName();
        }
	}

	public String onlineAuth(String authData, String responseCode) {
		byte[] b = BytesUtils.asc2hex(authData);//9F360211ED910A80923B3E263AE39B3030
		List<byte[]> tags = new ArrayList<>();
		EmvDealOnlineRspResult r = Controler.getInstance().emvDealOnlineRsp(true, b, b.length, responseCode);
		if (r.authResult== EnumEmvDealOnlineRsp.SUCC) {
			tags.add(new byte[]{(byte) 0x9F, (byte) 0x26});
			tags.add(new byte[]{(byte) 0x95});
			tags.add(new byte[]{(byte) 0x4F});
			tags.add(new byte[]{(byte) 0x5F, (byte) 0x34});
			tags.add(new byte[]{(byte) 0x9B});
			tags.add(new byte[]{(byte) 0x9F, (byte) 0x36});
			tags.add(new byte[]{(byte) 0x82});
			tags.add(new byte[]{(byte) 0x9F, (byte) 0x37});
			tags.add(new byte[]{(byte) 0x50});
			GetEmvDataResult rdata = Controler.getInstance().getEmvData(tags, false);
			if (rdata.commResult.equals(EnumCommRet.NOERROR)) {
				return BytesUtils.hex2asc(rdata.tlvData);
			} else {
				return r.commResult.toDisplayName();
			}
		} else {
			return r.commResult.toDisplayName();
		}
	}
	
	public String cancel() {
		Controler.getInstance().cancelComm();
		return "Cancel by user";
	}
	
	public String showText() {
		String msg = "Hello World!";
		Controler.getInstance();
		Controler.setText(msg, 30);
		return "showText success";
	}
	public String[] multipartToFile(MultipartFile[] multipartFile)
	{
		String[] result = new String[multipartFile.length]; ;
		for(int i=0; i<multipartFile.length;i++)
		{
			File file = new File("src/main/resources/"+multipartFile[i].getOriginalFilename());

			try (OutputStream os = new FileOutputStream(file)) {
				System.out.println("data size: "+multipartFile[i].getBytes().length);
			    os.write(multipartFile[i].getBytes());
			    System.out.println("data size: "+file.exists());
			    result[i]="src/main/resources/"+multipartFile[i].getOriginalFilename();
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				break;
			}
		}
		return result;
		
	}
	
	public String setBitmap(MultipartFile[] multipartFile) {
		//EnumCommRet result=EnumCommRet.NOERROR;
		 String[] isWrite=multipartToFile( multipartFile);
		 if(isWrite.length!=multipartFile.length)
			{
				return "Uploading failed";
				
			}
		 else
		 {
			 final Display display = Display.getDefault();
			for(int i=0;i<isWrite.length;i++)
			{
		    Image image = new Image(display, isWrite[i]);
			Controler.setBitmap(EnumBitmapLocation.ROM, 1, 0, 0, 128, 16, image);
			
			}
		 }
		
	
		     return "Image uploaded successfuly";
}
	
	public String setKeyIndex(KeyIndexModel keyIndexModel) {
		if(keyIndexModel.getKeyIndex()==null)
		{
			return "Invalid key Index";
		}
		else
		{
			String msg="Set key index success";
			switch (keyIndexModel.getKeyIndex()) {
			case 0:
				Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX0);
				break;
case 1:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX1);	
				break;
case 2:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX2);
	break;
case 3:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX3);
	break;
case 4:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX4);
	break;
case 5:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX5);
	break;
case 6:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX6);
	break;
case 7:
	Controler.getInstance().setKeyIndex(EnumKeyIndex.INDEX7);
	break;
			default:
				msg="Invalid Index! Please enter index between 0 to 7";
				break;
			}
			return msg;
		}
		
		
	}
	
    public class UpdatePosProc implements IUpdatePosProc {
        InputStream fs;
       // TipListener listener;
        
        public UpdatePosProc(InputStream fs) {
            // TODO Auto-generated constructor stub
            this.fs = fs;
           // this.listener = listener;
        }

        @Override
        public void UpdateProcess(final int totalSize, final int alreadySize) {
        	//listener.onTip("Upgrading..." +  alreadySize  + "/" + totalSize);
        }

        @Override
        public int totalsize() throws IOException {
            // TODO Auto-generated method stub
            return this.fs.available();
        }

        @Override
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            // TODO Auto-generated method stub
            return this.fs.read(buffer, byteOffset, byteCount);
        }
    }

    public String updateFirmware(MultipartFile file) {
        try {
           
                UpdatePosProc updateProc = new UpdatePosProc(new ByteArrayInputStream(file.getBytes()));
                UpdatePosResult result = Controler.getInstance().UpdatePos(updateProc);
                if (result.isComplete()) {
                    return "updated Successfuly";
                }
            //} 
            //listener.onTip("Upgrade file not exist!");
            return "Upgrade file not exist!";
            
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed";
        }
        //return false;
    }
	/* Vendor verification process */
    public String readSignData(String mSignFilePath) {
        try {
            InputStream inputStream;
            File file = new File(mSignFilePath);
            if (!file.exists()) {
                return null;
            }
            inputStream = new FileInputStream(file);
           return BytesUtil.bytes2Hex( BytesUtil.input2byte(inputStream));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public String encryptByPrivateKey(String plainData,MultipartFile path) {
        try {
        	//byte[] data =Files.readAllBytes(path);
            PrivateKey privateKey = RSAUtil.loadPrivateKey(new ByteArrayInputStream(path.getBytes()));
            byte[] mSignData = RSAUtil.encrypt(BytesUtil.hexString2ByteArray(plainData), privateKey);
           return BytesUtil.bytes2Hex(mSignData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String verifyVendor(String plainData,String signData)
    {
    	boolean ret = Controler.vendorVerify(BytesUtil.hexString2ByteArray(plainData),
                BytesUtil.hexString2ByteArray(signData));
    	if(ret)
    		return "Vendor verification success";
    	else
    		return "Vendor verification fail";
    }
}
