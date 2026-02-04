package com.mf.mp63.controler;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mf.mp63.services.LoadKeysServices;

@RestController
public class MFLoadKeysControler {

	
	@Autowired
	LoadKeysServices services;
	
	@RequestMapping(value="/load_kek",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadKek(@RequestBody  LoadKekModel kekModel)
	{
		return services.loadKek(kekModel);
	}
	

	@RequestMapping(value="/load_master_key",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadMasterKey(@RequestBody LoadMasterKeyModel loadMasterKeyModel)
	{
		return services.loadMasterKey(loadMasterKeyModel);
	}
	
	@RequestMapping(value="/load_work_key",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadWorkKey(@RequestBody LoadWorkKeyModel loadWorkKeyModel)
	{
		return services.loadWorkKey(loadWorkKeyModel);
	}

	/*Implementation of TR31*/
//	@RequestMapping(value="/load_tr31_tmk",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseModel loadTR31TMK(@RequestBody LoadTR31TMK loadTR31TMK)
//	{
//		return services.loadTR31TMK(loadTR31TMK);
//	}
	@RequestMapping(value="/load_tr31_kek",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadTR31TMK(@RequestBody LoadTR31Kek loadTR31Kek)
	{
		return services.loadTR31Kek(loadTR31Kek);
	}

	@RequestMapping(value="/load_tr31_pin_block",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel loadWorkKey(@RequestBody TR31PinBlock tr31PinBlock)
	{
		return services.loadTR31PinBlock(tr31PinBlock);
	}


	/*Implementation END*/
	

	@RequestMapping(value="/cal_mac",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel calMac(@RequestBody CalculateMacModel calculateMacModel)
	{
		return services.calMac(calculateMacModel);
	}
	
	@RequestMapping(value="/key_index",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseModel setKeyIndex(@RequestBody KeyIndexModel keyIndexModel)
	{
		return services.setKeyIndex(keyIndexModel);
	}
}
