package com.in.novopay.hdfc.mf.pos.controler;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.in.novopay.hdfc.mf.pos.services.SetEMVParamService;

import model.DukptModel;
import model.ResponseModel;

@RestController
public class SetEMVParamControler {
	
	@Autowired
	private SetEMVParamService setEMVParamService;
	
	
	@RequestMapping(value="/load_aid",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadAID(@RequestBody ArrayList<String> aids) {
		ResponseModel responseModel=new ResponseModel();
		responseModel.setData(setEMVParamService.loadAID(aids));
		responseModel.setStatusCode(200);
		return responseModel;
	}
	
	
	@RequestMapping(value="/load_capk",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadCAPK(@RequestBody ArrayList<String> capks)
	{
		
		ResponseModel responseModel=new ResponseModel();
		responseModel.setData(setEMVParamService.loadCAPK(capks));
		responseModel.setStatusCode(200);
		return responseModel;
	}
	

	@RequestMapping(value="/init_dukpt",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel initDUKPT(@RequestBody DukptModel dukptModel)
	{
		return setEMVParamService.initDUKPT(dukptModel);
	}
	@RequestMapping(value = "/load_emv_param", params = "emvParam", method = RequestMethod.GET)
	public String connetDevice(@RequestParam("emvParam") String emvParam)
	{
		System.out.print("emvParam: "+ emvParam);
		return setEMVParamService.loadEmvParam(emvParam);
	}
	
	@RequestMapping(value="/load_image",method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
	public String loadBitmap(@RequestParam("file") MultipartFile[] multipartFile)
	{
		System.out.println("Request contains, File: " + multipartFile.length);
		return setEMVParamService.loadBitmap(multipartFile);
	}
	

}
