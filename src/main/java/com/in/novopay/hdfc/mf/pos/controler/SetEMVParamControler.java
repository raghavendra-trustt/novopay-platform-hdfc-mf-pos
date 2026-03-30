package com.in.novopay.hdfc.mf.pos.controler;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in.novopay.hdfc.mf.pos.request.PosSetupRequest;
import com.in.novopay.hdfc.mf.pos.response.PosSetUpResponse;
import com.in.novopay.hdfc.mf.pos.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.in.novopay.hdfc.mf.pos.services.SetEMVParamService;

import model.DukptModel;
import model.ResponseModel;

@RestController
public class SetEMVParamControler {
	
	@Autowired
	private SetEMVParamService setEMVParamService;

	@Autowired
	private CardService cardService;
	
	
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
	
	@RequestMapping(value="/load_image",method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String loadBitmap(@RequestParam("file") MultipartFile[] multipartFile)
	{
		System.out.println("Request contains, File: " + multipartFile.length);
		return setEMVParamService.loadBitmap(multipartFile);
	}

	@RequestMapping(value="/load_default_image",method = RequestMethod.GET)
	public String loadBitmapImages()
	{
		return setEMVParamService.loadBitmapImages();
	}

	@PostMapping(path= "/posSetup")
	public PosSetUpResponse posSetup(@RequestBody PosSetupRequest posSetupRequest){
		return cardService.setPosSetup(posSetupRequest);
	}
	

}
