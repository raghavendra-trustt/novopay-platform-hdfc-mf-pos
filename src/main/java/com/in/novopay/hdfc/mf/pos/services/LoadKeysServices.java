package com.in.novopay.hdfc.mf.pos.services;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.novopay.hdfc.mf.pos.DeviceHelper;

@Service
public class LoadKeysServices {
	
	@Autowired
	DeviceHelper deviceHelper;
	
	public ResponseModel loadKek(LoadKekModel loadKekModel)
	{
		
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(String.valueOf(deviceHelper.loadKek(loadKekModel)));
        responseModel.setStatusCode(200);
		return responseModel;
	}
	
	public ResponseModel loadMasterKey(LoadMasterKeyModel loadMasterKeyModel)
	{
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(String.valueOf(deviceHelper.loadMasterKey(loadMasterKeyModel)));
        responseModel.setStatusCode(200);
		
		return responseModel;
	}
	
	public ResponseModel loadWorkKey(LoadWorkKeyModel loadWorkKeyModel)
	{
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(String.valueOf(deviceHelper.loadWorkKey(loadWorkKeyModel)));
        responseModel.setStatusCode(200);
		return responseModel;
	}

	/*Implementation of TR31*/
//	public ResponseModel loadTR31TMK(LoadTR31TMK loadTR31TMK)
//	{
//		ResponseModel responseModel=new ResponseModel();
//		responseModel.setData(deviceHelper.loadTR31TMK(loadTR31TMK));
//		responseModel.setStatusCode(200);
//		return responseModel;
//	}
	public  ResponseModel loadTR31Kek(LoadTR31Kek loadTR31Kek)
	{
		ResponseModel responseModel=new ResponseModel();
		responseModel.setData(deviceHelper.loadTR31Kek(loadTR31Kek));
		responseModel.setStatusCode(200);
		return responseModel;
	}
	public ResponseModel loadTR31PinBlock(TR31PinBlock tr31PinBlock)
	{

		ResponseModel responseModel=new ResponseModel();
		responseModel.setData(deviceHelper.loadTR31PinBlock(tr31PinBlock));
		responseModel.setStatusCode(200);
		return responseModel;

	}

	/*Implementation END of TR31*/

	public ResponseModel calMac(CalculateMacModel calculateMacModel)
	{
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(deviceHelper.calcMac(calculateMacModel));
        responseModel.setStatusCode(200);
		return responseModel;
	}

	public ResponseModel setKeyIndex(KeyIndexModel keyIndexModel) {
		
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(deviceHelper.setKeyIndex(keyIndexModel));
        responseModel.setStatusCode(200);
        
		return responseModel;
	}

}
