package com.mf.mp63.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mf.mp63.DeviceHelper;
import com.morefun.mpos.sdk.constants.EnumCommRet;
import com.morefun.mpos.sdk.result.ReadCardResult;

import model.CardReadResponse;
import model.ReadCardModel;

@Service
public class CardReadingService {
    
    @Autowired
    DeviceHelper deviceHelper;

	public CardReadResponse readCard(ReadCardModel readCardModel) throws IllegalArgumentException, IllegalAccessException {
		ReadCardResult result=deviceHelper.readCard(readCardModel);
		CardReadResponse cardReadResponse=new CardReadResponse();
		if (!result.commResult.equals(EnumCommRet.NOERROR)) {
			cardReadResponse.setResponseMessage("Read card error");
			cardReadResponse.setStatusCode("201");
            return cardReadResponse;
        } else {
            switch (result.cardType) {
                case 0:
                	cardReadResponse.setResponseMessage("Canceled by User");
        			cardReadResponse.setStatusCode("201");
                    break;
                case 1:
                case 2:
                case 3:
                	cardReadResponse.setResponseMessage("Success ");
        			cardReadResponse.setStatusCode("200");
                    //StringBuilder builder = new StringBuilder();
                    if (result.cardType == 1) {
                        //builder.append("\ncardType:" + "Mag Card");
                    	cardReadResponse.setCardType("Mag Card");
                        //Mag Card
                    } else if (result.cardType == 2) {
                        //IC Card
                        //builder.append("\ncardType:" + "IC Card");
                        cardReadResponse.setCardType("IC Card");
                    } else if (result.cardType == 3) {
                        //RF Card
                       // builder.append("\ncardType:" + "RF Card");
                    	cardReadResponse.setCardType("RF Card");
                    }

//                    builder.append("\npan:" + result.pan);
//                    builder.append("\npansn:" + result.pansn);
//                    builder.append("\npinBlock:" + result.pinblock);
//                    builder.append("\ntrack2:" + result.track2);
//                    builder.append("\ntrack3:" + result.track3);
//                    builder.append("\nicData:" + result.icData);
//                    builder.append("\nexpData:" + result.expData);
//                    builder.append("\nksn:" + result.ksn);
//                    builder.append("\nmac_ksn:" + result.mac_ksn);
//                    builder.append("\nmag_ksn:" + result.mag_ksn);
//                    builder.append("\npin_ksn:" + result.pin_ksn);
                    cardReadResponse.setData(result);
                    //state=builder.toString();
                    break;

                case 4:
                	cardReadResponse.setResponseMessage("Need insert ic card");
        			cardReadResponse.setStatusCode("200");
                	//state="Need insert ic card";
                    //Need Insert ICCard
                    break;
                case 5:
                    //TimeOut
//                	state="Read card timeout";
                	cardReadResponse.setResponseMessage("Read card timeout");
        			cardReadResponse.setStatusCode("201");
                    break;
                case 6:
                	cardReadResponse.setResponseMessage("Read card error");
        			cardReadResponse.setStatusCode("201");
                	//state="Read card error";
                    //read error
                    break;
                default:
                    break;
            }

        }
		return cardReadResponse;
		
	
	}

	public String performOnlineAuth(String authData) {
		return deviceHelper.onlineAuth(authData,"");
	}

}
