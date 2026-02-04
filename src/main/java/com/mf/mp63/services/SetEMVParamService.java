package com.mf.mp63.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mf.mp63.DeviceHelper;

import model.DukptModel;
import model.ResponseModel;


@Service
public class SetEMVParamService {
	
	@Autowired
	DeviceHelper deviceHelper;

	public String loadAID(ArrayList<String> aids) {
		
		return deviceHelper.downLoadAid(aids);
	}

	public String loadCAPK(ArrayList<String> capks) {
		
		return deviceHelper.downLoadCapk(capks);
	}

	public ResponseModel initDUKPT(DukptModel dukptModel) {
		ResponseModel responseModel=new ResponseModel();
        responseModel.setData(deviceHelper.loadDukpt(dukptModel));
        responseModel.setStatusCode(200);
		return responseModel;
	}
	public String loadEmvParam(String emvParam)
	{
		return String.valueOf(deviceHelper.setEmvParam(emvParam));
	}
	
	public String loadBitmap(MultipartFile[] multipartFile)
	{
		return deviceHelper.setBitmap(multipartFile);
		
	}


}
